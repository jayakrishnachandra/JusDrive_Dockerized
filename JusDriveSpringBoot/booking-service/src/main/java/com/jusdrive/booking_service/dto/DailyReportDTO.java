package com.jusdrive.booking_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Getter
@Setter
public class DailyReportDTO {
    private LocalDate date;
    private long bookingsCount;
    private String dateLabel; // Format “05 Aug (Wed)”

    public DailyReportDTO(LocalDate date, long bookingsCount) {
        this.date = date;
        this.bookingsCount = bookingsCount;
        this.dateLabel = formatDateLabel(date);
    }

    private String formatDateLabel(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd MMM (EEE)", Locale.ENGLISH));
    }
}
