package com.example.booking.repository;

import com.example.booking.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ResourceRepository extends JpaRepository<Resource, Long> {

    // 1. Find resources by type
    List<Resource> findByType(String type);

    // 2. Find only active resources
    List<Resource> findByActiveTrue();

    // 3. Check if resource exists by name
    boolean existsByName(String name);
}

