package com.jusdrive.booking_service.mapper;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.jusdrive.booking_service.client.CarServiceClient;
import com.jusdrive.booking_service.dto.BookingWithCarDTO;
import com.jusdrive.booking_service.dto.CarResponse;
import com.jusdrive.booking_service.entity.Booking;

import java.util.Base64;


@Component
public class BookingMapper {

    private static final Logger logger = LoggerFactory.getLogger(BookingMapper.class);

    private final CarServiceClient carServiceClient;

    public BookingMapper(CarServiceClient carServiceClient) {
        this.carServiceClient = carServiceClient;
    }

    public BookingWithCarDTO toDto(Booking booking) {
        CarResponse carDetails = carServiceClient.getCarById(booking.getCarId());
        byte[] carImage = null;

        try {
            carImage = carServiceClient.getCarImage(booking.getCarId());
        } catch (Exception e) {
            logger.warn("No image available for car ID: {}. Using default image or null.", booking.getCarId());
        }

        BookingWithCarDTO dto = new BookingWithCarDTO();
        dto.setBookingId(booking.getBookingId());
        dto.setCustomerId(booking.getCustomerId());
        dto.setBookingDate(booking.getBookingDate());
        dto.setReturnDate(booking.getReturnDate());
        dto.setTotalAmount(booking.getTotalAmount());
        dto.setStatus(booking.getStatus().toString());
        dto.setCustomerEmail(booking.getCustomerEmail());

        // Car details
        dto.setCarId(carDetails.getCarId());
        dto.setCarModel(carDetails.getModel());
        dto.setCarBrand(carDetails.getBrand());
        dto.setRegistrationNumber(carDetails.getRegistrationNumber());
        dto.setColor(carDetails.getColor());
        dto.setCarType(carDetails.getCarType());
        dto.setSeatingCapacity(carDetails.getSeatingCapacity());
        dto.setPricePerDay(carDetails.getPricePerDay());

        if (carImage != null) {
            dto.setImage(Base64.getEncoder().encodeToString(carImage));
        }

        return dto;
    }

}
