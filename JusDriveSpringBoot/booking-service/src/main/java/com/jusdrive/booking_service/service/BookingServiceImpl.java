package com.jusdrive.booking_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jusdrive.booking_service.client.CarServiceClient;
import com.jusdrive.booking_service.client.NotificationClient;
import com.jusdrive.booking_service.client.NotificationRequest;
import com.jusdrive.booking_service.dto.BookingWithCarDTO;
import com.jusdrive.booking_service.dto.CarAvailabilityResponse;
import com.jusdrive.booking_service.dto.CarResponse;
import com.jusdrive.booking_service.dto.DailyReportDTO;
import com.jusdrive.booking_service.dto.MonthlyReportDTO;
import com.jusdrive.booking_service.dto.WeeklyReportDTO;
import com.jusdrive.booking_service.entity.Booking;
import com.jusdrive.booking_service.mapper.BookingMapper;
import com.jusdrive.booking_service.repository.BookingRepository;
import com.jusdrive.booking_service.util.HtmlTemplates;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CarServiceClient carServiceClient;

    @Autowired
    private NotificationClient notificationClient;

    @Autowired
    private BookingMapper bookingMapper;

    @Override
    public Booking createBooking(Booking booking) {

    CarAvailabilityResponse availabilityResponse = carServiceClient.checkCarAvailability(booking.getCarId());
    if (!availabilityResponse.isAvailable()) {
        throw new RuntimeException("Car is not available for booking");
    }

    CarResponse carDetails = carServiceClient.getCarById(booking.getCarId());
    Booking savedBooking = bookingRepository.save(booking);

    try {
        carServiceClient.updateCarStatus(savedBooking.getCarId(), "BOOKED");
    } catch (Exception e) {
         // Rollback: delete the booking
        bookingRepository.deleteById(savedBooking.getBookingId());
        throw new RuntimeException("Booking failed due to car status update error. Rolled back.");
    }

    String htmlMessage = HtmlTemplates.bookingConfirmationEmail(savedBooking, carDetails);
    NotificationRequest notificationRequest = new NotificationRequest(
            savedBooking.getCustomerEmail(),
            "Booking Confirmation",
            htmlMessage
    );


    notificationClient.sendNotification(notificationRequest);
    return savedBooking;
}

    @Override
    public Booking updateBookingStatus(Integer bookingId, Booking.Status status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    return new RuntimeException("Booking not found");
                });

        booking.setStatus(status);
        Booking updatedBooking = bookingRepository.save(booking);

        String htmlMessage = HtmlTemplates.bookingStatusUpdateEmail(updatedBooking);
        

        //email
        NotificationRequest notificationRequest = new NotificationRequest(
                updatedBooking.getCustomerEmail(),
                "Booking Status Update",
                 htmlMessage);
        notificationClient.sendNotification(notificationRequest);

        return updatedBooking;
    }

    public List<BookingWithCarDTO> getBookingsByCustomer(Integer customerId) {
        List<Booking> bookings = bookingRepository.findByCustomerId(customerId);
        logger.info("Found {} bookings for Customer ID: {}", bookings.size(), customerId);

        return bookings.stream()
                .map(booking -> bookingMapper.toDto(booking))
                .toList();
    }


    @Override
    public List<BookingWithCarDTO> getBookingsByOwner(Long ownerId) {
        List<CarResponse> ownerCars = carServiceClient.getOwnerCars(ownerId.toString());
        if (ownerCars == null || ownerCars.isEmpty()) {
            logger.warn("No cars found for Owner ID: {}", ownerId);
            return List.of();
        }

        return ownerCars.stream()
            .flatMap(car -> bookingRepository.findByCarId(car.getCarId())
                .stream()
                .map(booking -> bookingMapper.toDto(booking)))
            .toList();
        }


    @Override
    public List<Booking> getBookingsByCar(Integer carId) {
        List<Booking> bookings = bookingRepository.findByCarId(carId);
        logger.info("Found {} bookings for Car ID: {}", bookings.size(), carId);
        return bookings;
    }

    @Override
    public List<DailyReportDTO> getDailyReport(Long ownerId, int days) {
        List<Integer> carIds = carServiceClient.getOwnerCars(ownerId.toString())
            .stream().map(c -> c.getCarId()).toList();

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(days - 1);
        LocalDateTime from = startDate.atStartOfDay();
        LocalDateTime to = LocalDateTime.now();

        List<Booking> bookings = bookingRepository.findByCarIdInAndBookingDateBetween(carIds, from, to);

        Map<LocalDate, Long> counts = bookings.stream().collect(Collectors.groupingBy(
                b -> b.getBookingDate().toLocalDate(),
                Collectors.counting()
            ));

        List<DailyReportDTO> report = new ArrayList<>(days);
        for (int i = 0; i < days; i++) {
            LocalDate d = startDate.plusDays(i);
            report.add(new DailyReportDTO(d, counts.getOrDefault(d, 0L)));
        }
        return report;
    }

    @Override
    public List<WeeklyReportDTO> getWeeklyReport(Long ownerId, int weeks) {
        List<Integer> carIds = carServiceClient.getOwnerCars(ownerId.toString())
            .stream().map(c -> c.getCarId()).toList();

        LocalDate today = LocalDate.now();
        WeekFields wf = WeekFields.of(Locale.getDefault());
        LocalDate firstWeekStart = today.minusWeeks(weeks - 1).with(wf.dayOfWeek(), 1);
        LocalDateTime from = firstWeekStart.atStartOfDay();
        LocalDateTime to = LocalDateTime.now();

        List<Booking> bookings = bookingRepository.findByCarIdInAndBookingDateBetween(carIds, from, to);

        Map<Integer, Long> weekCounts = bookings.stream().collect(Collectors.groupingBy(
                b -> b.getBookingDate()
                       .toLocalDate()
                       .get(wf.weekOfWeekBasedYear()),
                Collectors.counting()
            ));

        List<WeeklyReportDTO> report = new ArrayList<>(weeks);
        for (int i = 0; i < weeks; i++) {
            LocalDate start = firstWeekStart.plusWeeks(i);
            LocalDate end = start.plusDays(6);
            int weekNum = start.get(wf.weekOfWeekBasedYear());
            report.add(new WeeklyReportDTO(weekNum, start, end,
                    weekCounts.getOrDefault(weekNum, 0L)));
        }
        return report;
    }

    @Override
    public List<MonthlyReportDTO> getMonthlyReport(Long ownerId, int months) {
        List<Integer> carIds = carServiceClient.getOwnerCars(ownerId.toString())
            .stream().map(c -> c.getCarId()).collect(Collectors.toList());

        YearMonth thisMonth = YearMonth.now();
        YearMonth startMonth = thisMonth.minusMonths(months - 1);
        LocalDateTime from = startMonth.atDay(1).atStartOfDay();
        LocalDateTime to = LocalDateTime.now();

        List<Booking> bookings = bookingRepository.findByCarIdInAndBookingDateBetween(carIds, from, to);

        Map<YearMonth, Long> monthCounts = bookings.stream().collect(Collectors.groupingBy(
                b -> YearMonth.from(b.getBookingDate()),
                Collectors.counting()
            ));

        List<MonthlyReportDTO> report = new ArrayList<>(months);
        for (int i = 0; i < months; i++) {
            YearMonth ym = startMonth.plusMonths(i);
            report.add(new MonthlyReportDTO(
                ym.getMonthValue(),
                ym.getYear(),
                monthCounts.getOrDefault(ym, 0L))
            );
        }
        return report;
    }
}