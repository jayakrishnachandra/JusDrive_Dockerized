package com.jusdrive.CarService.repository;

import com.jusdrive.CarService.entity.Car;
import com.jusdrive.CarService.entity.CarStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Integer> {
    List<Car> findByOwnerId(Long Id);
    long countByOwnerId(Long ownerId);
    long countByOwnerIdAndStatus(Long ownerId, CarStatus status);
    List<Car> findByStatus(CarStatus status);
    boolean existsByRegistrationNumber(String registrationNumber);

    @Query("SELECT c FROM Car c WHERE " +
       "(" +
       "LOWER(c.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
       "LOWER(c.model) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
       "CAST(c.seatingCapacity AS string) LIKE CONCAT('%', :keyword, '%') OR " +
       "LOWER(c.color) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
       "LOWER(c.carType) LIKE LOWER(CONCAT('%', :keyword, '%'))" +
       ") AND c.status = 'AVAILABLE'")
    List<Car> searchAvailableCarsByKeyword(@Param("keyword") String keyword);

    Page<Car> findByStatus(CarStatus status, Pageable pageable);
}
