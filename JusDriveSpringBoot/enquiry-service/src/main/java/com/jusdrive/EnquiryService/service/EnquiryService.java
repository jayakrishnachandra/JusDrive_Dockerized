package com.jusdrive.EnquiryService.service;

import java.util.List;
import java.util.Optional;

import com.jusdrive.EnquiryService.dto.EnquiryWithCarAndResponseDTO;
import com.jusdrive.EnquiryService.entity.Enquiry;
import com.jusdrive.EnquiryService.entity.EnquiryResponse;

public interface EnquiryService {
    Enquiry createEnquiry(Enquiry enquiry);
    EnquiryResponse respondToEnquiry(int enquiryId, EnquiryResponse response);
    List<Enquiry> getAllEnquiries();
    List<Enquiry> getEnquiriesByCustomer(int customerId);
    Optional<EnquiryResponse> getResponseByEnquiryId(int enquiryId);
    List<Enquiry> getEnquiriesByCarId(int carId);
    List<EnquiryWithCarAndResponseDTO> getEnquiriesWithDetailsByCustomer(int customerId);
    List<EnquiryWithCarAndResponseDTO> getEnquiriesByOwner(Long ownerId);

}
