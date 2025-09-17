package com.example.booking.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Resource resource;

    @ManyToOne(optional = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private BigDecimal price;
    private Instant startTime;
    private Instant endTime;
    private Instant createdAt;
    private Instant updatedAt;
}
