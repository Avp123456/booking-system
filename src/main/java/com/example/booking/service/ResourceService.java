package com.example.booking.service;

import com.example.booking.model.Resource;
import com.example.booking.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public Page<Resource> listAll(int page, int size, Sort sort) {
        Pageable pageable = PageRequest.of(page, size, sort);
        return resourceRepository.findAll(pageable);
    }

    public Resource create(Resource r) {
        return resourceRepository.save(r);
    }

    public Optional<Resource> findById(Long id) {
        return resourceRepository.findById(id);
    }

    public Resource update(Long id, Resource updated) {
        var existing = resourceRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Resource not found"));
        existing.setName(updated.getName());
        existing.setType(updated.getType());
        existing.setDescription(updated.getDescription());
        existing.setCapacity(updated.getCapacity());
        existing.setActive(updated.isActive());
        return resourceRepository.save(existing);
    }

    public void delete(Long id) {
        resourceRepository.deleteById(id);
    }
}
