package com.jusDrive.auth_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jusDrive.auth_service.entity.Customer;
import com.jusDrive.auth_service.repository.CustomerRepository;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public Customer findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    public Customer save(Customer customer) {
       return customerRepository.save(customer);
    }

    public Customer findById(long userId) {
       return customerRepository.findById(userId);
    }
}