package com.jusDrive.auth_service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.jusDrive.auth_service.dto.OtpDTO;
import com.jusDrive.auth_service.entity.Customer;
import com.jusDrive.auth_service.exception.UserNotFoundException;
import com.jusDrive.auth_service.util.HtmlTemplates;
import com.jusDrive.auth_service.util.JwtUtils;
import com.jusDrive.auth_service.mailClient.NotificationClient;
import com.jusDrive.auth_service.mailClient.NotificationRequest;
import com.jusDrive.auth_service.service.CustomerService;
import com.jusDrive.auth_service.service.OtpService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth/customer")
@Validated
public class CustomerAuthController {

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private OtpService otpService = new OtpService();
    @Autowired
    private NotificationClient notificationClient;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Customer customer) {
        Customer existingCustomer = customerService.findByEmail(customer.getEmail());
        if (existingCustomer == null || !passwordEncoder.matches(customer.getPassword(), existingCustomer.getPassword())) {
            throw new RuntimeException("Authentication failed");
        }

        String token = jwtUtils.generateToken(String.valueOf(existingCustomer.getId()));
        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "Login successful");
        response.put("token", token);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody @Valid Customer customer) {
        if (customerService.findByEmail(customer.getEmail()) != null) {
            throw new RuntimeException("Customer already exists");
        }

        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customerService.save(customer);

        String token = jwtUtils.generateToken(String.valueOf(customer.getId()));

        String message = HtmlTemplates.welcomeEmail(customer.getName());
        notificationClient.sendNotification(new NotificationRequest(
            customer.getEmail(),
            "Welcome to JusDrive",
            message
        ));

        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "Registration successful");
        response.put("token", token);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserData(@RequestHeader("X-User-Id") String userId) {
        if (userId == null) {
            throw new RuntimeException("Missing X-User-Id header");
        }

        Customer customer = customerService.findById(Long.parseLong(userId));
        if (customer == null) {
            throw new UserNotFoundException("Customer not found");
        }

            
        Map<String, Object> response = new HashMap<>();
        response.put("success", "true");
        response.put("customer", customer);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody OtpDTO otpDTO) {
        String otp = otpService.generateOtp(otpDTO.getEmail());
        String message = HtmlTemplates.passwordResetEmail(otp);
        notificationClient.sendNotification(new NotificationRequest(
            otpDTO.getEmail(),
            "Password Reset Request",
            message
        ));

        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "Password reset code sent to email");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/verifyResetCode")
    public ResponseEntity<Map<String, String>> verifyResetCode(@RequestBody OtpDTO otpDTO) {
        boolean isValid = otpService.validateOtp(otpDTO.getEmail(), otpDTO.getOtp());
        if (!isValid) {
            throw new RuntimeException("Invalid or expired reset code");
        }

        Customer customer = customerService.findByEmail(otpDTO.getEmail());
        if (customer == null) {
            throw new UserNotFoundException("Customer not found for email: " + otpDTO.getEmail());
        }

        String token = jwtUtils.generateToken(customer.getId().toString());
        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "Reset code validated successfully");
        response.put("token", token);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestHeader("X-User-Id") String userId, @RequestBody Customer customer) {
        Customer existingCustomer = customerService.findById(Long.parseLong(userId));
        if (existingCustomer == null) {
            throw new UserNotFoundException("No account found for user ID: " + userId);
        }

        existingCustomer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customerService.save(existingCustomer);

        String token = jwtUtils.generateToken(userId);
        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "Password reset successful");
        response.put("token", token);

        return ResponseEntity.ok(response);
    }
}
