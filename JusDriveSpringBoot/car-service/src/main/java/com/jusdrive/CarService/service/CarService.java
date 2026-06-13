package com.jusdrive.CarService.service;

import com.jusdrive.CarService.entity.Car;
import com.jusdrive.CarService.entity.CarStatus;
import com.jusdrive.CarService.entity.Owner;
import com.jusdrive.CarService.exception.*;
import com.jusdrive.CarService.repository.CarRepository;
import com.jusdrive.CarService.repository.OwnerRepository;
import com.jusdrive.CarService.response.CarAvailabilityResponse;
import com.jusdrive.CarService.utils.ImageUtils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.PageImpl;

import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final OwnerRepository ownerRepository;

    public CarService(CarRepository carRepository, OwnerRepository ownerRepository) {
        this.carRepository = carRepository;
        this.ownerRepository = ownerRepository;
    }



    public Car addCar(Car car, Long ownerId)
    {
        if (ownerId == null) {
            throw new InvalidInputException("Owner ID is required");
        }
        if (carRepository.existsByRegistrationNumber(car.getRegistrationNumber())) {
            throw new DuplicateRegistrationException("Car with this registration number already exists.");
        }
 
        Optional<Owner> optionalOwner = ownerRepository.findById(ownerId);
 
        Owner owner = null;
        if (optionalOwner.isPresent()) {
            owner = optionalOwner.get();
        } else {
            throw new OwnerNotFoundException("OwnerId " + ownerId + " not found");
        }
 
        car.setOwner(owner);
        car.setCreatedAt(LocalDateTime.now());
        car.setModifiedAt(LocalDateTime.now());
 
        return carRepository.save(car);
    }
 
    public Car updateCarImage(Integer carId, MultipartFile image) throws IOException {
        Optional<Car> optionalCar = carRepository.findById(carId);
        if (optionalCar.isEmpty()) {
            throw new CarNotFoundException("CarId: " + carId + " not found");
        }

        Car car = optionalCar.get();

        if (image == null || image.isEmpty()) {
            throw new InvalidInputException("Invalid image file for carId: " + carId);
        }

        car.setImage(ImageUtils.compressImage(image.getBytes()));
        car.setModifiedAt(LocalDateTime.now());
        return carRepository.save(car);
    }

    public byte[] getCarImage(Integer carId) {
        Optional<Car> optionalCar = carRepository.findById(carId);
        if (optionalCar.isEmpty()) {
            throw new CarNotFoundException("CarId:" + carId + " not found");
        }

        Car car = optionalCar.get();
        byte[] image = car.getImage();

        if (image == null) {
            throw new InvalidInputException("No image available for carId: " + carId);
        }

        return ImageUtils.decompressImage(image);
    }

    public Car updateCar(Integer carId, Car updatedCar, Long ownerId) {
        if (carId == null || ownerId == null) {
            throw new InvalidInputException("CarId and OwnerId are required");
        }

        Optional<Car> optionalCar = carRepository.findById(carId);
        if (optionalCar.isEmpty()) {
            throw new CarNotFoundException("CarId: " + carId + " not found");
        }

        Car car = optionalCar.get();

        if (!ownerId.equals(car.getOwner().getId())) {
            throw new UnauthorizedActionException("Unauthorized update attempt for carId: " + carId);
        }

        if (updatedCar.getBrand() != null)
            car.setBrand(updatedCar.getBrand());
        if (updatedCar.getModel() != null)
            car.setModel(updatedCar.getModel());
        if (updatedCar.getRegistrationNumber() != null)
            car.setRegistrationNumber(updatedCar.getRegistrationNumber());
        if (updatedCar.getYear() != null)
            car.setYear(updatedCar.getYear());
        if (updatedCar.getColor() != null)
            car.setColor(updatedCar.getColor());
        if (updatedCar.getCarType() != null)
            car.setCarType(updatedCar.getCarType());
        if (updatedCar.getSeatingCapacity() != null)
            car.setSeatingCapacity(updatedCar.getSeatingCapacity());
        if (updatedCar.getPricePerDay() != null)
            car.setPricePerDay(updatedCar.getPricePerDay());
        if (updatedCar.getStatus() != null)
            car.setStatus(updatedCar.getStatus());

        car.setModifiedAt(LocalDateTime.now());
        return carRepository.save(car);
    }

    public List<Car> getCarsByOwner(Long ownerId) {
        if (ownerId == null) {
            throw new InvalidInputException("Owner ID is required");
        }

        Optional<Owner> optionalOwner = ownerRepository.findById(ownerId);
        if (optionalOwner.isEmpty()) {
            throw new OwnerNotFoundException("OwnerId: " + ownerId + " not found");
        }

        List<Car> cars = carRepository.findByOwnerId(ownerId);
        return cars;
    }

    public String deleteCar(Integer carId, Long ownerId) {
        Optional<Car> optionalCar = carRepository.findById(carId);
        if (optionalCar.isEmpty()) {
            throw new CarNotFoundException("CarId: " + carId + " not found");
        }

        Car car = optionalCar.get();

        if (!ownerId.equals(car.getOwner().getId())) {
            throw new UnauthorizedActionException("Unauthorized delete attempt for car ID: " + carId);
        }

        carRepository.deleteById(carId);
        return "Car with carId: "+ carId + " deleted successfully";
    }


    public Car updateCarStatus(Integer carId, String status) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car not found with ID: " + carId));
    
        try {
            CarStatus carStatus = CarStatus.valueOf(status.toUpperCase());
            car.setStatus(carStatus);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid car status: " + status);
        }
    
        return carRepository.save(car);
    }
    


    public Car toggleStatus(Integer carId) {
        Optional<Car> optionalCar = carRepository.findById(carId);
        if (optionalCar.isEmpty()) {
            throw new CarNotFoundException("CarId: " + carId + " not found");
        }
        Car car = optionalCar.get();
        if(car.getStatus() == CarStatus.AVAILABLE)
            car.setStatus(CarStatus.INACTIVE);
        else
            car.setStatus(CarStatus.AVAILABLE);

        return carRepository.save(car);
    }

    public Map<String, Object> getCarStatisticsByOwner(Long ownerId) {
        long total = carRepository.countByOwnerId(ownerId);
        long available = carRepository.countByOwnerIdAndStatus(ownerId, CarStatus.AVAILABLE);
        long booked = carRepository.countByOwnerIdAndStatus(ownerId, CarStatus.BOOKED);
        long inactive = carRepository.countByOwnerIdAndStatus(ownerId, CarStatus.INACTIVE);
     
        Map<String, Object> stats = new HashMap<>();
        stats.put("ownerId", ownerId);
        stats.put("totalCars", total);
        stats.put("availableCars", available);
        stats.put("bookedCars", booked);
        stats.put("inactiveCars", inactive);
        return stats;
     
    }

    public Car getCarById(Integer carId) {
        return carRepository.findById(carId)
                .orElseThrow(() -> {
                    return new CarNotFoundException("Car with ID " + carId + " not found");
                });
    }


    public List<Car> searchCarsByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<Car> searchedCars = carRepository.searchAvailableCarsByKeyword(keyword);
        return searchedCars.stream().map((car) ->
        {
            byte[] carImage = car.getImage();
            car.setImage(carImage != null ? ImageUtils.decompressImage(carImage) : null);
            return car;
        }
        ).toList();
    }



    public CarAvailabilityResponse checkCarAvailability(Integer carId) {
        Optional<Car> car = carRepository.findById(carId);
        if (car.isEmpty()) {
            return new CarAvailabilityResponse(carId, false);
        }
        Car carEntity = car.get();
        boolean isAvailable = "AVAILABLE".equalsIgnoreCase(carEntity.getStatus().toString());
        return new CarAvailabilityResponse(carId, isAvailable);
    }

    public Page<Car> getAvailableCars(int pg, int size) {
        
        PageRequest pageRequest = PageRequest.of(pg, size);
        Page<Car> availableCars = carRepository.findByStatus(CarStatus.AVAILABLE, pageRequest);
    
        if (availableCars.isEmpty()) {
            
            return Page.empty(); //empty
        }
        List<Car> carDTOs = availableCars.stream().map(car -> {
            byte[] carImage = car.getImage();
            Car carDTO = new Car();
            carDTO.setCarId(car.getCarId());
            carDTO.setImage(carImage != null ? ImageUtils.decompressImage(carImage) : null);
            carDTO.setBrand(car.getBrand());
            carDTO.setModel(car.getModel());
            carDTO.setRegistrationNumber(car.getRegistrationNumber());
            carDTO.setColor(car.getColor());
            carDTO.setCarType(car.getCarType());
            carDTO.setSeatingCapacity(car.getSeatingCapacity());
            carDTO.setPricePerDay(car.getPricePerDay());
            carDTO.setStatus(car.getStatus());
            carDTO.setYear(car.getYear());
            carDTO.setCreatedAt(car.getCreatedAt());
            carDTO.setModifiedAt(car.getModifiedAt());
            carDTO.setOwner(car.getOwner());
            return carDTO;
        }).toList();
    
        return new PageImpl<>(carDTOs, pageRequest, availableCars.getTotalElements());
    }
    


}
