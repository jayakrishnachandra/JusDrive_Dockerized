package com.jusdrive.EnquiryService.service;
 
import com.jusdrive.EnquiryService.client.CarServiceClient;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jusdrive.EnquiryService.dto.EnquiryWithCarAndResponseDTO;
import com.jusdrive.EnquiryService.entity.Enquiry;
import com.jusdrive.EnquiryService.entity.EnquiryResponse;
import com.jusdrive.EnquiryService.exception.EnquiryNotFoundException;
import com.jusdrive.EnquiryService.repository.EnquiryRepository;
import com.jusdrive.EnquiryService.repository.EnquiryResponseRepository;
 
@Service
public class EnquiryServiceImpl implements EnquiryService {
    @Autowired
    private EnquiryRepository enquiryRepo;
 
    @Autowired
    private EnquiryResponseRepository responseRepo;

    @Autowired
    private CarServiceClient carServiceClient;
 
    @Override
    public Enquiry createEnquiry(Enquiry enquiry) {
        return enquiryRepo.save(enquiry);
    }

        @Override
    public List<EnquiryWithCarAndResponseDTO> getEnquiriesWithDetailsByCustomer(int customerId) {
        List<Enquiry> enquiries = enquiryRepo.findByCustomerId(customerId);

        return enquiries.stream().map(enquiry -> {
            EnquiryWithCarAndResponseDTO dto = new EnquiryWithCarAndResponseDTO();

  
            dto.setEnquiryId(enquiry.getEnquiryId());
            dto.setCustomerId(enquiry.getCustomerId());
            dto.setMessage(enquiry.getMessage());
            dto.setCreatedAt(enquiry.getCreatedAt());
            dto.setStatus(enquiry.getStatus().toString());

            //car
            CarServiceClient.CarResponse carDetails = carServiceClient.getCarById(enquiry.getCarId());
            dto.setCarId(carDetails.getCarId());
            dto.setCarModel(carDetails.getModel());
            dto.setCarBrand(carDetails.getBrand());
            dto.setRegistrationNumber(carDetails.getRegistrationNumber());
            dto.setColor(carDetails.getColor());
            dto.setCarType(carDetails.getCarType());
            dto.setSeatingCapacity(carDetails.getSeatingCapacity());
            dto.setPricePerDay(carDetails.getPricePerDay());

           
            try {
                byte[] carImage = carServiceClient.getCarImage(enquiry.getCarId());
                dto.setImage(Base64.getEncoder().encodeToString(carImage));
            } catch (Exception e) {
                dto.setImage(null);
            }

            Optional<EnquiryResponse> response = responseRepo.findByEnquiry_EnquiryId(enquiry.getEnquiryId());
            response.ifPresent(res -> {
                dto.setResponse(res.getResponse());
                dto.setRespondedAt(res.getRespondedAt());
            });

            return dto;
        }).toList();
    }
 
    @Override
    public EnquiryResponse respondToEnquiry(int enquiryId, EnquiryResponse response) {
       
        Enquiry enquiry = enquiryRepo.findById(enquiryId)
                .orElseThrow(() -> {
                    return new EnquiryNotFoundException("Enquiry not found with ID: " + enquiryId);
                });
 
        enquiry.setStatus(Enquiry.Status.replied);
        enquiry.setModifiedAt(LocalDateTime.now());
        
        enquiryRepo.save(enquiry);
        response.setEnquiry(enquiry);
        return responseRepo.save(response);
    }
 
    @Override
    public List<Enquiry> getAllEnquiries() {
        return enquiryRepo.findAll();
    }
 
    @Override
    public List<Enquiry> getEnquiriesByCustomer(int customerId) {
        List<Enquiry> enquiries = enquiryRepo.findByCustomerId(customerId);
   
        if (enquiries.isEmpty()) {
            throw new EnquiryNotFoundException("No enquiries found for customer ID: " + customerId);
        }
   
        return enquiries;
    }
   
 
    @Override
    public Optional<EnquiryResponse> getResponseByEnquiryId(int enquiryId) {
        Optional<EnquiryResponse> response = responseRepo.findByEnquiry_EnquiryId(enquiryId);
   
        if (response.isEmpty()) {
            throw new EnquiryNotFoundException("No response found for enquiry ID: " + enquiryId);
        }
   
        return response;
    }
   
 
    @Override
    public List<Enquiry> getEnquiriesByCarId(int carId) {
        List<Enquiry> enquiries = enquiryRepo.findByCarId(carId);
   
        if (enquiries.isEmpty()) {
            throw new EnquiryNotFoundException("No enquiries found for car ID: " + carId);
        }
   
        return enquiries;
    }

    @Override
    public List<EnquiryWithCarAndResponseDTO> getEnquiriesByOwner(Long ownerId) {


        List<CarServiceClient.CarResponse> cars = carServiceClient.getOwnerCars(ownerId.toString());
        
        return cars.stream().flatMap(car -> {
            List<Enquiry> enquiries = enquiryRepo.findByCarId(car.getCarId());
            if (enquiries.isEmpty()) {
            return null;
            }

            return enquiries.stream().map(enquiry -> {
            EnquiryWithCarAndResponseDTO dto = new EnquiryWithCarAndResponseDTO();
            dto.setCarId(car.getCarId());
            dto.setCarModel(car.getModel());
            dto.setCarBrand(car.getBrand());
            dto.setRegistrationNumber(car.getRegistrationNumber());
            dto.setColor(car.getColor());
            dto.setCarType(car.getCarType());
            dto.setSeatingCapacity(car.getSeatingCapacity());
            dto.setPricePerDay(car.getPricePerDay());

            try {
                byte[] carImage = carServiceClient.getCarImage(car.getCarId());
                dto.setImage(Base64.getEncoder().encodeToString(carImage));
            } catch (Exception e) {
                dto.setImage(null);
            }

            dto.setEnquiryId(enquiry.getEnquiryId());
            dto.setCustomerId(enquiry.getCustomerId());
            dto.setMessage(enquiry.getMessage());
            dto.setCreatedAt(enquiry.getCreatedAt());
            dto.setStatus(enquiry.getStatus().toString());

            Optional<EnquiryResponse> response = responseRepo.findByEnquiry_EnquiryId(enquiry.getEnquiryId());
            response.ifPresent(res -> {
                dto.setResponse(res.getResponse());
                dto.setRespondedAt(res.getRespondedAt());
            });

            return dto;
            });
        }).filter(dto -> dto != null).toList();
    }
}

