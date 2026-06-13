package com.jusdrive.EnquiryService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnquiryWithCarAndResponseDTO {
    private int enquiryId;
    private int customerId;
    private String message;
    private LocalDateTime createdAt;
    private String status;

    // Car details
    private int carId;
    private String carModel;
    private String carBrand;
    private String registrationNumber;
    private String color;
    private String carType;
    private int seatingCapacity;
    private double pricePerDay;
    private String image; // Base64

    // Response
    private String response;
    private LocalDateTime respondedAt;
}
