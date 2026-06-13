package com.jusdrive.EnquiryService.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jusdrive.EnquiryService.dto.EnquiryWithCarAndResponseDTO;
import com.jusdrive.EnquiryService.entity.Enquiry;
import com.jusdrive.EnquiryService.entity.EnquiryResponse;
import com.jusdrive.EnquiryService.service.EnquiryService;

@RestController
// @CrossOrigin(origins = "*")
@RequestMapping("/api/enquiries")
public class EnquiryController {

    @Autowired
    private EnquiryService enquiryService;

    @PostMapping("/new")
    public ResponseEntity<Enquiry> createEnquiry(@RequestBody Enquiry enquiry) {
       
        Enquiry savedEnquiry = enquiryService.createEnquiry(enquiry);
        
        return ResponseEntity.ok(savedEnquiry);
    }

    @PostMapping("/{id}/response")
    public ResponseEntity<EnquiryResponse> respond(@PathVariable int id, @RequestBody EnquiryResponse response) {
       
        EnquiryResponse savedResponse = enquiryService.respondToEnquiry(id, response);
       
        return ResponseEntity.ok(savedResponse);
    }

    @GetMapping("/all")
    public List<Enquiry> getAll() {
        return enquiryService.getAllEnquiries();  
    }

    @GetMapping("/customer")
    public List<Enquiry> getByCustomer(@RequestHeader("X-User-Id") String userIdHeader) {
        int customerId = Integer.parseInt(userIdHeader);
        return enquiryService.getEnquiriesByCustomer(customerId);
        
    }

        @GetMapping("/customer/details")
    public List<EnquiryWithCarAndResponseDTO> getEnquiriesWithDetailsByCustomer(@RequestHeader("X-User-Id") String userIdHeader) {

        int customerId = Integer.parseInt(userIdHeader);

        return enquiryService.getEnquiriesWithDetailsByCustomer(customerId);
    }

    @GetMapping("/{id}/response")
    public ResponseEntity<EnquiryResponse> getResponse(@PathVariable int id) {
     
        Optional<EnquiryResponse> response = enquiryService.getResponseByEnquiryId(id);

        if (response.isPresent()) {
               return ResponseEntity.ok(response.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/car/{carId}")
    public List<Enquiry> getByCarId(@PathVariable int carId) {
        return enquiryService.getEnquiriesByCarId(carId);
        
    }

    @GetMapping("/owner")
    public List<EnquiryWithCarAndResponseDTO> getEnquiriesByOwner(@RequestHeader("X-User-Id") String userIdHeader) {
        Long ownerId = Long.parseLong(userIdHeader);
        return enquiryService.getEnquiriesByOwner(ownerId);
    }
}
