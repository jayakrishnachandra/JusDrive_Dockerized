package com.jusdrive.booking_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingWithCarDTO {
    private Integer bookingId;
    private Integer customerId;
    private LocalDateTime bookingDate;
    private LocalDateTime returnDate;
    private Double totalAmount;
    private String status;
    private String customerEmail;

    // Car 
    private Integer carId;
    private String carModel;
    private String carBrand;
    private String registrationNumber;
    private String color;
    private String carType;
    private Integer seatingCapacity;
    private Double pricePerDay;
    private String image; //base64
}
