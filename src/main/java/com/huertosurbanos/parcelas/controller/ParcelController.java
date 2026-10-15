package com.huertosurbanos.parcelas.controller;

import com.huertosurbanos.parcelas.dto.ParcelRequest;
import com.huertosurbanos.parcelas.dto.ParcelResponse;
import com.huertosurbanos.parcelas.service.ParcelService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/parcels")
public class ParcelController {

    private final ParcelService parcelService;

    public ParcelController(ParcelService parcelService) {
        this.parcelService = parcelService;
    }

    @PostMapping
    public ResponseEntity<ParcelResponse> create(@Valid @RequestBody ParcelRequest request) {
        ParcelResponse created = parcelService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ResponseEntity<List<ParcelResponse>> findAll() {
        return ResponseEntity.ok(parcelService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParcelResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(parcelService.findById(id));
    }
}
