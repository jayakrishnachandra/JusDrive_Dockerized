package com.jusdrive.notification_service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

	private String toEmail;
	
    private String subject;
    
    private String message;
	
}
