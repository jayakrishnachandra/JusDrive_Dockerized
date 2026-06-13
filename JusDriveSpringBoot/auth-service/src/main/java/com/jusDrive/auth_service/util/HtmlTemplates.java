package com.jusDrive.auth_service.util;

public class HtmlTemplates {

    public static String welcomeEmail(String name) {
        return """
            <html>
                <body>
                    <h2>Welcome, %s!</h2>
                    <p>Your account has been successfully created.</p>
                    <p>We're glad to have you with us.</p>
                    <p>Regards,<br>JusDrive Team</p>
                </body>
            </html>
        """.formatted(name);
    }

    public static String passwordResetEmail(String otp) {
        return """
            <html>
                <body style='font-family: Arial, sans-serif; text-align: center; padding: 20px;'>
                    <h2>Password Reset Request</h2>
                    <p>Your password reset code is: <b>%s</b></p>
                    <p>This code is valid for 5 minutes.</p>
                </body>
            </html>
        """.formatted(otp);
    }
}
