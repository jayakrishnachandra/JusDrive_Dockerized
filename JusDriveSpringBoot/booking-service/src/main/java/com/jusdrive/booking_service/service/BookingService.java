package com.jusdrive.booking_service.service;


import java.util.List;

import com.jusdrive.booking_service.dto.BookingWithCarDTO;
import com.jusdrive.booking_service.dto.DailyReportDTO;
import com.jusdrive.booking_service.dto.MonthlyReportDTO;
import com.jusdrive.booking_service.dto.WeeklyReportDTO;
import com.jusdrive.booking_service.entity.Booking;

public interface BookingService {
    Booking createBooking(Booking booking);
    Booking updateBookingStatus(Integer bookingId, Booking.Status status);
    List<BookingWithCarDTO> getBookingsByCustomer(Integer customerId);
    List<Booking> getBookingsByCar(Integer carId);
    List<BookingWithCarDTO> getBookingsByOwner(Long ownerId);

     List<DailyReportDTO>  getDailyReport(Long ownerId, int days);
    List<WeeklyReportDTO> getWeeklyReport(Long ownerId, int weeks);
    List<MonthlyReportDTO> getMonthlyReport(Long ownerId, int month);


}