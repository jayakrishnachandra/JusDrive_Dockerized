package com.jusdrive.booking_service.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.jusdrive.booking_service.dto.CarAvailabilityResponse;
import com.jusdrive.booking_service.dto.CarResponse;

@FeignClient(name = "car-service")
public interface CarServiceClient {

    @GetMapping("/api/cars/{carId}/availability")
    CarAvailabilityResponse checkCarAvailability(@PathVariable Integer carId);

    @GetMapping("/api/cars/{carId}")
    CarResponse getCarById(@PathVariable Integer carId);

    @PutMapping("/api/cars/{carId}/status")
    CarResponse updateCarStatus(@PathVariable("carId") Integer carId, @RequestParam("status") String status);

    @GetMapping("/api/cars/getCarImage/{carId}")
    byte[] getCarImage(@PathVariable Integer carId);
    
    @GetMapping("/api/cars/owner")
    List<CarResponse> getOwnerCars(@RequestHeader("X-User-Id") String userIdHeader);
    

}