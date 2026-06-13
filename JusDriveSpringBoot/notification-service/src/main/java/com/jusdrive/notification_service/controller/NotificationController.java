package com.jusdrive.notification_service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jusdrive.notification_service.exception.GlobalExceptionHandler;
import com.jusdrive.notification_service.request.NotificationRequest;
import com.jusdrive.notification_service.service.NotificationService;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/alert")
public class NotificationController {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@Autowired
	private NotificationService notificationService;

	@GetMapping("/msg")
	public String getMsg() {
		return "Hello";
	}


		@PostMapping("/send")
	public ResponseEntity<String> sendMessage(@RequestBody NotificationRequest request) throws MessagingException {
		logger.info("Received notification request for: {}", request.getToEmail());
		notificationService.sendMessage(request);
		return ResponseEntity.ok("Notification is being processed asynchronously.");
	}


}
