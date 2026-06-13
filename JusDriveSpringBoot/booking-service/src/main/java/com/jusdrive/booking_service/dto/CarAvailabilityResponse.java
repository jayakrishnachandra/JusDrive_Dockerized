package com.jusdrive.booking_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CarAvailabilityResponse {
    private Integer carId;
    private boolean available;
}
