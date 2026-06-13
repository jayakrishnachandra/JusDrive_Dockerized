package com.jusDrive.auth_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jusDrive.auth_service.entity.Owner;
import com.jusDrive.auth_service.repository.OwnerRepository;

@Service
public class OwnerService {

    @Autowired
    private OwnerRepository ownerRepository;

    public Owner findByEmail(String email) {
        return ownerRepository.findByEmail(email);
    }

    public Owner save(Owner owner) {
        return ownerRepository.save(owner);
    }

    public Owner findById(long userId) {
       return ownerRepository.findById(userId);
    }
}