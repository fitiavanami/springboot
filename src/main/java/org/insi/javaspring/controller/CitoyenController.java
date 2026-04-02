package org.insi.javaspring.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.insi.javaspring.dto.CitoyenDTO;
import org.insi.javaspring.model.Citoyen;
import org.insi.javaspring.service.CitoyenServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citoyen")
@RequiredArgsConstructor
public class CitoyenController {

    private final CitoyenServices service;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<Citoyen> addCitoyen(@Valid @RequestBody CitoyenDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.saveFromDto(dto));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Citoyen>> getAll() {
        return ResponseEntity.ok(service.getAllCitoyens());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Citoyen> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<Citoyen> update(@PathVariable Long id, @Valid @RequestBody CitoyenDTO dto) {
        return ResponseEntity.ok(service.updateCitoyen(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}