package com.example.booking.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReservationRequest {
    private Long resourceId;
    private String startTime;  
    private String endTime;    
    private BigDecimal price;
    private String status;     // PENDING, CONFIRMED, CANCELLED
}
