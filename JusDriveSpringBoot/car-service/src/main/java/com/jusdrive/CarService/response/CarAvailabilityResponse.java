package com.jusdrive.CarService.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CarAvailabilityResponse {
    private Integer carId;
    private boolean available;
}
