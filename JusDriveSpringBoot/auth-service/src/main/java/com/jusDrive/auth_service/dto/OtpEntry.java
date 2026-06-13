package com.jusDrive.auth_service.dto;

import java.time.LocalDateTime;

public class OtpEntry {
    private final String otp;
    private final LocalDateTime expiryTime;

    public OtpEntry(String otp, LocalDateTime expiryTime) {
        this.otp = otp;
        this.expiryTime = expiryTime;
    }

    public String getOtp() {
        return otp;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }
}