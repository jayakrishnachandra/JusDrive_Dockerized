package com.jusdrive.notification_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.jusdrive.notification_service.request.NotificationRequest;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class NotificationServiceImpl implements NotificationService {

	private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Override
    @Async
    public void sendMessage(NotificationRequest request) throws MessagingException{
        logger.info("Attempting to send notification");


        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setTo(request.getToEmail());
        helper.setSubject(request.getSubject());
        helper.setText(request.getMessage(), true); // true = HTML content

        mailSender.send(mimeMessage);
        logger.info("Email sent successfully");

    }

}
