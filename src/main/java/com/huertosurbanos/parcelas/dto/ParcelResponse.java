package com.huertosurbanos.parcelas.dto;

import com.huertosurbanos.parcelas.model.SoilType;

import java.time.Instant;
import java.util.UUID;

public record ParcelResponse(
        UUID id,
        String name,
        double latitude,
        double longitude,
        SoilType soilType,
        String ownerOrLead,
        Instant createdAt
) {
}
