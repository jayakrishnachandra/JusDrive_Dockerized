package com.jusdrive.booking_service.util;

import com.jusdrive.booking_service.dto.CarResponse;
import com.jusdrive.booking_service.entity.Booking;

public class HtmlTemplates {
    
    public static String bookingConfirmationEmail(Booking booking, CarResponse car) {
        return """
            <html>
                <body style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;'>
                    <div style='max-width: 600px; margin: auto; background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);'>
                        <h2 style='color: #2c3e50;'>Your car is %s!</h2>
                        <p style='font-size: 16px;'>Thank you for choosing JusDrive. Here are your booking details:</p>
                        <table style='width: 100%%; border-collapse: collapse; font-size: 14px;'>
                            <tr><td style='padding: 8px; font-weight: bold;'>Car:</td><td style='padding: 8px;'>%s %s</td></tr>
                            <tr><td style='padding: 8px; font-weight: bold;'>Color:</td><td style='padding: 8px;'>%s</td></tr>
                            <tr><td style='padding: 8px; font-weight: bold;'>Type:</td><td style='padding: 8px;'>%s</td></tr>
                            <tr><td style='padding: 8px; font-weight: bold;'>Seating Capacity:</td><td style='padding: 8px;'>%d</td></tr>
                            <tr><td style='padding: 8px; font-weight: bold;'>Price Per Day:</td><td style='padding: 8px;'>₹%.2f</td></tr>
                            <tr><td style='padding: 8px; font-weight: bold;'>Total Amount:</td><td style='padding: 8px;'>₹%.2f</td></tr>
                        </table>
                        <p style='margin-top: 20px; font-size: 14px; color: #555;'>We hope you enjoy your ride. Safe travels!</p>
                    </div>
                </body>
            </html>
        """.formatted(
            booking.getStatus(),
            car.getBrand(), car.getModel(),
            car.getColor(),
            car.getCarType(),
            car.getSeatingCapacity(),
            car.getPricePerDay(),
            booking.getTotalAmount()
        );
    }

    public static String bookingStatusUpdateEmail(Booking booking) {
        return """
            <html>
                <body>
                    <h1>Booking Status Update</h1>
                    <p>Your booking status has been updated!</p>
                    <p><strong>New Status:</strong> %s</p>
                </body>
            </html>
        """.formatted(
            booking.getStatus()
        );
    }
}
