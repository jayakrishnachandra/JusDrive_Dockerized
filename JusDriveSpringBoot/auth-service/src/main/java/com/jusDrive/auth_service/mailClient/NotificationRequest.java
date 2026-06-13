
package com.jusDrive.auth_service.mailClient;

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
