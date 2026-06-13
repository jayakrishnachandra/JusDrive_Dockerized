package com.jusdrive.CarService.controller;

import com.jusdrive.CarService.entity.Car;
import com.jusdrive.CarService.response.CarAvailabilityResponse;
import com.jusdrive.CarService.service.CarService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
// @CrossOrigin(origins = "*")
@RequestMapping("/api/cars")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping("/{carId}")
    public ResponseEntity<Car> getCarById(@PathVariable Integer carId) {
        Car car = carService.getCarById(carId);
        return ResponseEntity.ok(car);
    }

    @GetMapping("/{carId}/availability")
    public ResponseEntity<CarAvailabilityResponse> checkCarAvailability(@PathVariable Integer carId) {
        CarAvailabilityResponse response = carService.checkCarAvailability(carId);

        if (response == null || !response.isAvailable()) {
            return ResponseEntity.ok(new CarAvailabilityResponse(carId, false));
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/newCar")
    public ResponseEntity<Car> newCar(@RequestHeader("X-User-Id") String userIdHeader, @RequestBody Car car) {
        long userId = Long.parseLong(userIdHeader);

        return ResponseEntity.ok(carService.addCar(car, userId));
    }

    @PutMapping("/updateCarImage")
    public ResponseEntity<Car> updateCarImage(@RequestParam Integer carId, @RequestParam MultipartFile image)
            throws IOException {

        return ResponseEntity.ok(carService.updateCarImage(carId, image));
    }

    @GetMapping("/getCarImage/{carId}")
    public ResponseEntity<byte[]> getCarImage(@PathVariable Integer carId) {

        byte[] image = carService.getCarImage(carId);

        if (image == null) {
    
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(image);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<Car>> getOwnerCars(@RequestHeader("X-User-Id") String userIdHeader) {
        long ownerId = Long.parseLong(userIdHeader);

        return ResponseEntity.ok(carService.getCarsByOwner(ownerId));
    }

    @PatchMapping("/{carId}/edit")
    public ResponseEntity<Car> editCarDetails(@RequestHeader("X-User-Id") String userIdHeader, @PathVariable Integer carId, @RequestBody Car car) {
        long ownerId = Long.parseLong(userIdHeader);

        return ResponseEntity.ok(carService.updateCar(carId, car, ownerId));
    }

    
    @DeleteMapping("/{carId}/remove")
    public ResponseEntity<Map<String, Object>> removeCar(@RequestHeader("X-User-Id") String userIdHeader, @PathVariable Integer carId) {
        long ownerId = Long.parseLong(userIdHeader);

        String response = carService.deleteCar(carId, ownerId);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("response", response);
        return ResponseEntity.ok(responseMap);
    }

    @PatchMapping("/{carId}/toggleStatus")
    public ResponseEntity<Car> toggleStatus(@PathVariable Integer carId) {
        return ResponseEntity.ok(carService.toggleStatus(carId));
    }

        @PutMapping("/{carId}/status")
    public ResponseEntity<Car> updateCarStatus(@PathVariable Integer carId, @RequestParam String status) {
        Car updatedCar = carService.updateCarStatus(carId, status);
        return ResponseEntity.ok(updatedCar);
    }


    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getCarStatisticsByOwner(@RequestHeader("X-User-Id") String userIdHeader) {
        long ownerId = Long.parseLong(userIdHeader);
        Map<String, Object> stats = carService.getCarStatisticsByOwner(ownerId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/available")
    public ResponseEntity<Map<String, Object>> getAvailableCars(@RequestParam int pg, @RequestParam int size) {
    
        Page<Car> carPage = carService.getAvailableCars(pg, size);
    
        if (carPage.isEmpty()) {

            return ResponseEntity.noContent().build();
        }
    
        Map<String, Object> response = new HashMap<>();
        response.put("cars", carPage.getContent());
        response.put("currentPage", carPage.getNumber());
        response.put("totalPages", carPage.getTotalPages());
        response.put("totalItems", carPage.getTotalElements());
    
        return ResponseEntity.ok(response);
    }
    

    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<Car>> searchCars(@PathVariable String keyword) {
        List<Car> results = carService.searchCarsByKeyword(keyword);
        return ResponseEntity.ok(results);
    }

    
}
