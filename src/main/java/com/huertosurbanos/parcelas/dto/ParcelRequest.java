package com.huertosurbanos.parcelas.dto;

import com.huertosurbanos.parcelas.model.SoilType;

public record ParcelRequest(
        String name,
        Double latitude,
        Double longitude,
        SoilType soilType,
        String ownerOrLead
) {
}
