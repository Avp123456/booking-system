package com.example.booking.controller;

import com.example.booking.model.Resource;
import com.example.booking.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<?> listResources(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort
    ) {
        String[] s = sort.split(",");
        Sort.Direction dir = Sort.Direction.fromString(s.length > 1 ? s[1] : "asc");
        Sort srt = Sort.by(dir, s[0]);
        var pageRes = resourceService.listAll(page, size, srt);
        return ResponseEntity.ok(pageRes);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<?> getResource(@PathVariable Long id) {
    	return resourceService.findById(id)
    	        .<ResponseEntity<?>>map(ResponseEntity::ok)
    	        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found"));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@RequestBody Resource resource) {
        var created = resourceService.create(resource);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Resource resource) {
        try {
            var updated = resourceService.update(id, resource);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        resourceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
