package com.jusdrive.EnquiryService.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jusdrive.EnquiryService.entity.Enquiry;

public interface EnquiryRepository extends JpaRepository<Enquiry, Integer> {
    List<Enquiry> findByCustomerId(int customerId);
    List<Enquiry> findByCarId(int carId);
}