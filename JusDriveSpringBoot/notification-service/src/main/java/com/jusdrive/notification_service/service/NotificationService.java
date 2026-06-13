package com.jusdrive.notification_service.service;


import org.springframework.stereotype.Service;

import com.jusdrive.notification_service.request.NotificationRequest;

import jakarta.mail.MessagingException;

@Service
public interface NotificationService {

	public void sendMessage(NotificationRequest request) throws MessagingException;
	
}
