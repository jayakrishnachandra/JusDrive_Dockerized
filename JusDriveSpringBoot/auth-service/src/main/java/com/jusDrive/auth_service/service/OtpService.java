
package com.jusDrive.auth_service.service;

import org.springframework.stereotype.Service;

import com.jusDrive.auth_service.exception.InvalidOtpException;
import com.jusDrive.auth_service.dto.*;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    // In-memory
    private final Map<String, OtpEntry> otpStorage = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

  
    public String generateOtp(String email) 
    {
        String otp = String.format("%06d", random.nextInt(1_000_000));
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);
        otpStorage.put(email, new OtpEntry(otp, expiryTime));
        return otp;
    }

    public boolean validateOtp(String email, String otp) 
    {
        OtpEntry storedEntry = otpStorage.get(email);
        if (storedEntry == null) {
            throw new InvalidOtpException("No OTP found for email: " + email);
        }

        if (storedEntry.getExpiryTime().isBefore(LocalDateTime.now())) {
            otpStorage.remove(email);
            throw new InvalidOtpException("OTP expired for email: " + email);
        }

        if (!storedEntry.getOtp().equals(otp)) {
            throw new InvalidOtpException("Invalid OTP entered for email: " + email);
        }

        otpStorage.remove(email); //all pass
        return true;
}

}

