package com.example.booking.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReservationRequest {
    private Long resourceId;
    private String startTime;  // ISO-8601 string
    private String endTime;    // ISO-8601 string
    private BigDecimal price;
    private String status;     // PENDING, CONFIRMED, CANCELLED
}
