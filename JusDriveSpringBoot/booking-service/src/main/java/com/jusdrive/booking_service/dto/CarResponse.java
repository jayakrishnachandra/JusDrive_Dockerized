package com.jusdrive.booking_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarResponse {
    private Integer carId;
    private String model;
    private String brand;
    private String registrationNumber;
    private String color;
    private String carType;
    private Integer seatingCapacity;
    private Double pricePerDay;
    private byte[] image;
}
