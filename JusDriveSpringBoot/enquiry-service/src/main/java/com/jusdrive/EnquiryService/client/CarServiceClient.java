package com.jusdrive.EnquiryService.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "car-service")
public interface CarServiceClient {

    @GetMapping("/api/cars/{carId}/availability")
    CarAvailabilityResponse checkCarAvailability(@PathVariable Integer carId);

    @GetMapping("/api/cars/{carId}")
    CarResponse getCarById(@PathVariable Integer carId);

     @GetMapping("/api/cars/owner")
    public List<CarResponse> getOwnerCars(@RequestHeader("X-User-Id") String userIdHeader);

    @GetMapping("/api/cars/getCarImage/{carId}")
    byte[] getCarImage(@PathVariable Integer carId);

    class CarResponse {
        private Integer carId;
        private String model;
        private String brand;
        private String registrationNumber;
        private String color;
        private String carType;
        private Integer seatingCapacity;
        private Double pricePerDay;
        private byte[] image;

        // Getters and Setters
        public Integer getCarId() {
            return carId;
        }

        public void setCarId(Integer carId) {
            this.carId = carId;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getRegistrationNumber() {
            return registrationNumber;
        }

        public void setRegistrationNumber(String registrationNumber) {
            this.registrationNumber = registrationNumber;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getCarType() {
            return carType;
        }

        public void setCarType(String carType) {
            this.carType = carType;
        }

        public Integer getSeatingCapacity() {
            return seatingCapacity;
        }

        public void setSeatingCapacity(Integer seatingCapacity) {
            this.seatingCapacity = seatingCapacity;
        }

        public Double getPricePerDay() {
            return pricePerDay;
        }

        public void setPricePerDay(Double pricePerDay) {
            this.pricePerDay = pricePerDay;
        }

        public byte[] getImage() {
            return image;
        }

        public void setImage(byte[] image) {
            this.image = image;
        }
    }

    class CarAvailabilityResponse {
        private Integer carId;
        private boolean available;

        // Getters and Setters
        public Integer getCarId() {
            return carId;
        }

        public void setCarId(Integer carId) {
            this.carId = carId;
        }

        public boolean isAvailable() {
            return available;
        }

        public void setAvailable(boolean available) {
            this.available = available;
        }
    }
}