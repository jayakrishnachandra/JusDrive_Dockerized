package com.jusdrive.booking_service.dto;

import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Getter
public class WeeklyReportDTO {
    private final int weekNumber;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final long bookingsCount;
    private final String weekLabel;

    public WeeklyReportDTO(int weekNumber, LocalDate startDate, LocalDate endDate, long bookingsCount) {
        this.weekNumber = weekNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.bookingsCount = bookingsCount;

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM", Locale.ENGLISH);
        this.weekLabel = String.format(
            "W%02d (%s – %s)",
            weekNumber,
            startDate.format(fmt),
            endDate.format(fmt)
        );
    }
}
