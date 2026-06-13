package com.jusdrive.EnquiryService.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jusdrive.EnquiryService.entity.EnquiryResponse;

public interface EnquiryResponseRepository extends JpaRepository<EnquiryResponse, Integer> {
    Optional<EnquiryResponse> findByEnquiry_EnquiryId(int enquiryId);
}