package com.jusdrive.booking_service;

import com.jusdrive.booking_service.client.CarServiceClient;
import com.jusdrive.booking_service.client.NotificationClient;
import com.jusdrive.booking_service.client.NotificationRequest;
import com.jusdrive.booking_service.dto.*;
import com.jusdrive.booking_service.entity.Booking;
import com.jusdrive.booking_service.mapper.BookingMapper;
import com.jusdrive.booking_service.repository.BookingRepository;
import com.jusdrive.booking_service.service.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;

import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock private BookingRepository bookingRepository;
    @Mock private CarServiceClient carServiceClient;
    @Mock private NotificationClient notificationClient;
    @Mock private BookingMapper bookingMapper;

    private Booking sampleBooking;
    private CarResponse sampleCar;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sampleBooking = new Booking();
        sampleBooking.setBookingId(1);
        sampleBooking.setCustomerId(101);
        sampleBooking.setCarId(202);
        sampleBooking.setBookingDate(LocalDateTime.now().minusDays(1));
        sampleBooking.setReturnDate(LocalDateTime.now().plusDays(2));
        sampleBooking.setTotalAmount(1500.0);
        sampleBooking.setStatus(Booking.Status.CONFIRMED);
        sampleBooking.setCustomerEmail("test@example.com");

        sampleCar = new CarResponse();
        sampleCar.setCarId(202);
        sampleCar.setModel("Test Model");
    }

    @Test
    void testCreateBooking_Success() {
        when(carServiceClient.checkCarAvailability(202)).thenReturn(new CarAvailabilityResponse(202,true));
        when(carServiceClient.getCarById(202)).thenReturn(sampleCar);
        when(bookingRepository.save(any())).thenReturn(sampleBooking);

        Booking result = bookingService.createBooking(sampleBooking);

        assertNotNull(result);
        verify(carServiceClient).updateCarStatus(202, "BOOKED");
        verify(notificationClient).sendNotification(any(NotificationRequest.class));
    }

   

    @Test
    void testUpdateBookingStatus_Success() {
        when(bookingRepository.findById(1)).thenReturn(Optional.of(sampleBooking));
        when(bookingRepository.save(any())).thenReturn(sampleBooking);

        Booking result = bookingService.updateBookingStatus(1, Booking.Status.CANCELLED);

        assertEquals(Booking.Status.CANCELLED, result.getStatus());
        verify(notificationClient).sendNotification(any(NotificationRequest.class));
    }

    @ParameterizedTest                         //practice
    @ValueSource( ints = {1,2,3,4})
    void testParameterized(int customerId)
    {
        assertTrue(customerId>0);
    }


    @Test
    void testGetBookingsByCustomer() {
        when(bookingRepository.findByCustomerId(101)).thenReturn(List.of(sampleBooking));
        when(bookingMapper.toDto(any())).thenReturn(new BookingWithCarDTO());

        List<BookingWithCarDTO> result = bookingService.getBookingsByCustomer(101);

        assertEquals(1, result.size());
    }

    @Test
    void testGetBookingsByOwner() {
        CarResponse car = new CarResponse();
        car.setCarId(202);
        when(carServiceClient.getOwnerCars("1")).thenReturn(List.of(car));
        when(bookingRepository.findByCarId(202)).thenReturn(List.of(sampleBooking));
        when(bookingMapper.toDto(any())).thenReturn(new BookingWithCarDTO());

        List<BookingWithCarDTO> result = bookingService.getBookingsByOwner(1L);

        assertEquals(1, result.size());
    }

    @Test
    void testGetDailyReport() {
        List<Booking> bookings = List.of(sampleBooking);
        when(carServiceClient.getOwnerCars("1")).thenReturn(List.of(sampleCar));
        when(bookingRepository.findByCarIdInAndBookingDateBetween(anyList(), any(), any()))
            .thenReturn(bookings);

        List<DailyReportDTO> report = bookingService.getDailyReport(1L, 3);

        assertEquals(3, report.size());
    }

    @Test
    void testGetWeeklyReport() {
        List<Booking> bookings = List.of(sampleBooking);
        when(carServiceClient.getOwnerCars("1")).thenReturn(List.of(sampleCar));
        when(bookingRepository.findByCarIdInAndBookingDateBetween(anyList(), any(), any()))
            .thenReturn(bookings);

        List<WeeklyReportDTO> report = bookingService.getWeeklyReport(1L, 2);

        assertEquals(2, report.size());
    }

    @Test
    void testGetMonthlyReport() {
        List<Booking> bookings = List.of(sampleBooking);
        when(carServiceClient.getOwnerCars("1")).thenReturn(List.of(sampleCar));
        when(bookingRepository.findByCarIdInAndBookingDateBetween(anyList(), any(), any()))
            .thenReturn(bookings);

        List<MonthlyReportDTO> report = bookingService.getMonthlyReport(1L, 2);

        assertEquals(2, report.size());
    }
}
