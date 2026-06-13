package com.jusdrive.booking_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jusdrive.booking_service.dto.BookingWithCarDTO;
import com.jusdrive.booking_service.dto.DailyReportDTO;
import com.jusdrive.booking_service.dto.MonthlyReportDTO;
import com.jusdrive.booking_service.dto.WeeklyReportDTO;
import com.jusdrive.booking_service.entity.Booking;
import com.jusdrive.booking_service.service.BookingService;

import java.util.List;

@RestController
// @CrossOrigin(origins = "*")
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/new")
    public ResponseEntity<Booking> createBooking(@RequestBody Booking booking, @RequestHeader("X-User-Id") String userId) {
        booking.setCustomerId(Integer.parseInt(userId));
        return ResponseEntity.ok(bookingService.createBooking(booking));
    }

    @PutMapping("/{bookingId}/status")
    public ResponseEntity<Booking> updateBookingStatus(@PathVariable Integer bookingId, @RequestParam Booking.Status status) {
        return ResponseEntity.ok(bookingService.updateBookingStatus(bookingId, status));
    }


    @GetMapping("/customer")
    public ResponseEntity<List<BookingWithCarDTO>> getBookingsByCustomer(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(
            bookingService.getBookingsByCustomer(Integer.parseInt(userId))
        );
    }


    @GetMapping("/car/{carId}")
    public ResponseEntity<List<Booking>> getBookingsByCar(@PathVariable Integer carId) {
        return ResponseEntity.ok(bookingService.getBookingsByCar(carId));
    }


    @GetMapping("/owner")
    public ResponseEntity<List<BookingWithCarDTO>> getBookingsByOwner(@RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(bookingService.getBookingsByOwner(Long.parseLong(userId)));
    }


    @GetMapping("/owner/reports/daily")
    public ResponseEntity<List<DailyReportDTO>> getDailyReport(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(
            bookingService.getDailyReport(Long.parseLong(userId), days)
        );
    }

    @GetMapping("/owner/reports/weekly")
    public ResponseEntity<List<WeeklyReportDTO>> getWeeklyReport(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(defaultValue = "4") int weeks) {
        return ResponseEntity.ok(
            bookingService.getWeeklyReport(Long.parseLong(userId), weeks)
        );
    }


    @GetMapping("/owner/reports/monthly")
    public ResponseEntity<List<MonthlyReportDTO>> getMonthlyReport(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(defaultValue = "12") int months) {
        return ResponseEntity.ok(
            bookingService.getMonthlyReport(Long.parseLong(userId), months)
        );
    }

}