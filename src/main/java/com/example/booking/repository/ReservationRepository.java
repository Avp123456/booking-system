package com.example.booking.repository;

import com.example.booking.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, JpaSpecificationExecutor<Reservation> {

    @Query("SELECT r FROM Reservation r WHERE r.resource.id = :resourceId AND r.status = 'CONFIRMED' AND r.startTime < :endTime AND r.endTime > :startTime")
    List<Reservation> findConfirmedOverlapping(@Param("resourceId") Long resourceId,
                                               @Param("startTime") Instant startTime,
                                               @Param("endTime") Instant endTime);
}
