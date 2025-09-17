package com.example.booking.service;

import com.example.booking.model.Reservation;
import com.example.booking.model.ReservationStatus;
import com.example.booking.repository.ReservationRepository;
import com.example.booking.repository.ResourceRepository;
import com.example.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.criteria.Predicate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;

    public Page<Reservation> filterReservations(Optional<ReservationStatus> status,
                                                Optional<BigDecimal> minPrice,
                                                Optional<BigDecimal> maxPrice,
                                                Optional<Long> userId,
                                                int page, int size, Sort sort) {

        Specification<Reservation> spec = (root, query, cb) -> {
            List<Predicate> preds = new ArrayList<>();

            status.ifPresent(s -> preds.add(cb.equal(root.get("status"), s)));
            if (minPrice.isPresent() && maxPrice.isPresent()) {
                preds.add(cb.between(root.get("price"), minPrice.get(), maxPrice.get()));
            } else if (minPrice.isPresent()) {
                preds.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice.get()));
            } else if (maxPrice.isPresent()) {
                preds.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice.get()));
            }

            userId.ifPresent(uid -> preds.add(cb.equal(root.get("user").get("id"), uid)));

            return cb.and(preds.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(page, size, sort);
        return reservationRepository.findAll(spec, pageable);
    }

    public Optional<Reservation> findById(Long id) {
        return reservationRepository.findById(id);
    }

    @Transactional
    public Reservation createReservation(Long resourceId, Long userId, ReservationStatus status,
                                         BigDecimal price, Instant start, Instant end, boolean enforceNoOverlap) {
        var resource = resourceRepository.findById(resourceId).orElseThrow(() -> new IllegalArgumentException("Resource not found"));
        var user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (start == null || end == null || !end.isAfter(start)) {
            throw new IllegalArgumentException("Invalid start/end time");
        }

        var now = Instant.now();

        Reservation r = Reservation.builder()
                .resource(resource)
                .user(user)
                .status(status)
                .price(price)
                .startTime(start)
                .endTime(end)
                .createdAt(now)
                .updatedAt(now)
                .build();

        return reservationRepository.save(r);
    }

    @Transactional
    public Reservation updateReservation(Long id, ReservationStatus status, BigDecimal price, Instant start, Instant end, boolean enforceNoOverlap) {
        var existing = reservationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        if (start != null && end != null && !end.isAfter(start)) {
            throw new IllegalArgumentException("Invalid start/end time");
        }

        if (status != null) existing.setStatus(status);
        if (price != null) existing.setPrice(price);
        if (start != null) existing.setStartTime(start);
        if (end != null) existing.setEndTime(end);
        existing.setUpdatedAt(Instant.now());
        return reservationRepository.save(existing);
    }

    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }
}
