package com.jusdrive.booking_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Getter
@Setter
public class MonthlyReportDTO {
    private int month;
    private int year;
    private long bookingsCount;
    private String monthLabel;

    public MonthlyReportDTO(int month, int year, long bookingsCount) {
        this.month = month;
        this.year = year;
        this.bookingsCount = bookingsCount;
        this.monthLabel = formatMonthLabel(month, year);
    }

    private String formatMonthLabel(int month, int year) {
        return YearMonth.of(year, month)
                .format(DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH));
    }
}
