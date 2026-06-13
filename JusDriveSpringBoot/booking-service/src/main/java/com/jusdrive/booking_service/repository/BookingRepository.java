package com.jusdrive.booking_service.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.jusdrive.booking_service.entity.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByCustomerId(Integer customerId);
    List<Booking> findByCarId(Integer carId);
     List<Booking> findByCarIdInAndBookingDateBetween(List<Integer> carIds, 
                                                     LocalDateTime start,
                                                     LocalDateTime end);
}