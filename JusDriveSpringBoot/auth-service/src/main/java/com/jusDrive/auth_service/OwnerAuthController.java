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
import com.jusDrive.auth_service.entity.Owner;
import com.jusDrive.auth_service.exception.UserNotFoundException;
import com.jusDrive.auth_service.util.HtmlTemplates;
import com.jusDrive.auth_service.util.JwtUtils;
import com.jusDrive.auth_service.mailClient.NotificationClient;
import com.jusDrive.auth_service.mailClient.NotificationRequest;
import com.jusDrive.auth_service.service.OwnerService;
import com.jusDrive.auth_service.service.OtpService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth/owner")
@Validated
public class OwnerAuthController {

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private OwnerService ownerService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private final OtpService otpService = new OtpService();
    @Autowired
    private NotificationClient notificationClient;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Owner owner) {
        Owner existingOwner = ownerService.findByEmail(owner.getEmail());
        if (existingOwner == null || !passwordEncoder.matches(owner.getPassword(), existingOwner.getPassword())) {
            throw new RuntimeException("Authentication failed");
        }

        String token = jwtUtils.generateToken(String.valueOf(existingOwner.getId()));
        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "Login successful");
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody @Valid Owner owner) {
        if (ownerService.findByEmail(owner.getEmail()) != null) {
            throw new RuntimeException("Owner already exists");
        }

        owner.setPassword(passwordEncoder.encode(owner.getPassword()));
        Owner savedOwner = ownerService.save(owner);

        String token = jwtUtils.generateToken(String.valueOf(savedOwner.getId()));

        String message = HtmlTemplates.welcomeEmail(owner.getName());
        notificationClient.sendNotification(new NotificationRequest(
            owner.getEmail(),
            "Welcome to JusDrive",
            message
        ));

        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "Registration successful");
        response.put("token", token);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/validateToken")
    public ResponseEntity<Map<String, String>> validateToken(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token format");
        }

        String token = authorizationHeader.substring(7);
        String email = jwtUtils.verifyToken(token);
        if (email == null) {
            throw new RuntimeException("Invalid token");
        }

        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "Token valid");
        response.put("email", email);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserData(@RequestHeader("X-User-Id") String userId) {
        if (userId == null) {
            throw new RuntimeException("Missing X-User-Id header");
        }

        Owner owner = ownerService.findById(Long.parseLong(userId));
        if (owner == null) {
            throw new UserNotFoundException("Owner not found");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", "true");
        response.put("owner", owner);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody OtpDTO otpDTO) {
        String otp = otpService.generateOtp(otpDTO.getEmail());
        String htmlFormattedMessage = HtmlTemplates.passwordResetEmail(otp);
        notificationClient.sendNotification(new NotificationRequest(
            otpDTO.getEmail(),
            "Password Reset Request",
            htmlFormattedMessage
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

        Owner owner = ownerService.findByEmail(otpDTO.getEmail());
        if (owner == null) {
            throw new UserNotFoundException("Owner not found for email: " + otpDTO.getEmail());
        }

        String token = jwtUtils.generateToken(owner.getId().toString());
        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "Reset code validated successfully");
        response.put("token", token);

        return ResponseEntity.ok(response);
    }
    @PostMapping("/resetPassword")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestHeader("X-User-Id") String userId, @RequestBody Owner owner) {
        Owner existingOwner = ownerService.findById(Long.parseLong(userId));
        if (existingOwner == null) {
            throw new UserNotFoundException("No account found for user ID: " + userId);
        }

        existingOwner.setPassword(passwordEncoder.encode(owner.getPassword()));
        ownerService.save(existingOwner);

        String token = jwtUtils.generateToken(userId);
        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "Password reset successful");
        response.put("token", token);

        return ResponseEntity.ok(response);
    }
}
