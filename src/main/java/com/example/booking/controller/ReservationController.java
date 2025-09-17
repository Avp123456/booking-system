package com.example.booking.controller;

import com.example.booking.dto.ReservationRequest;
import com.example.booking.model.ReservationStatus;
import com.example.booking.service.ReservationService;
import com.example.booking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<?> listReservations(
            @RequestParam Optional<ReservationStatus> status,
            @RequestParam Optional<BigDecimal> minPrice,
            @RequestParam Optional<BigDecimal> maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            Authentication authentication
    ) {
        String[] s = sort.split(",");
        Sort.Direction dir = Sort.Direction.fromString(s.length > 1 ? s[1] : "desc");
        Sort sortObj = Sort.by(dir, s[0]);

        Long userId = null;
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            var u = userService.findByUsername(authentication.getName());
            userId = u.getId();
        }

        var pageRes = reservationService.filterReservations(status, minPrice, maxPrice, Optional.ofNullable(userId), page, size, sortObj);
        return ResponseEntity.ok(pageRes);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<?> getReservation(@PathVariable Long id, Authentication authentication) {
        var resOpt = reservationService.findById(id);
        if (resOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reservation not found");

        var res = resOpt.get();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !res.getUser().getUsername().equals(authentication.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not allowed to view this reservation");
        }
        return ResponseEntity.ok(res);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<?> createReservation(@RequestBody ReservationRequest req, Authentication authentication) {
        Instant start = req.getStartTime() != null ? Instant.parse(req.getStartTime()) : null;
        Instant end = req.getEndTime() != null ? Instant.parse(req.getEndTime()) : null;
        BigDecimal price = req.getPrice();

        Long userId = userService.findByUsername(authentication.getName()).getId();

        ReservationStatus status = ReservationStatus.PENDING;
        if (req.getStatus() != null) {
            try {
                status = ReservationStatus.valueOf(req.getStatus().toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }

        try {
            var created = reservationService.createReservation(req.getResourceId(), userId, status, price, start, end, true);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateReservation(@PathVariable Long id, @RequestBody ReservationRequest req) {
        Instant start = req.getStartTime() != null ? Instant.parse(req.getStartTime()) : null;
        Instant end = req.getEndTime() != null ? Instant.parse(req.getEndTime()) : null;
        BigDecimal price = req.getPrice();
        ReservationStatus status = null;
        if (req.getStatus() != null) {
            status = ReservationStatus.valueOf(req.getStatus().toUpperCase());
        }
        try {
            var updated = reservationService.updateReservation(id, status, price, start, end, true);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}
