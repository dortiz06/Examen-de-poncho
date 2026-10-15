package com.huertosurbanos.parcelas.dto;

import com.huertosurbanos.parcelas.model.SoilType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ParcelRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 80, message = "El nombre debe tener entre 3 y 80 caracteres")
        String name,

        @NotNull(message = "La latitud es obligatoria")
        @DecimalMin(value = "-90.0", inclusive = true, message = "La latitud debe estar entre -90 y 90")
        @DecimalMax(value = "90.0", inclusive = true, message = "La latitud debe estar entre -90 y 90")
        Double latitude,

        @NotNull(message = "La longitud es obligatoria")
        @DecimalMin(value = "-180.0", inclusive = true, message = "La longitud debe estar entre -180 y 180")
        @DecimalMax(value = "180.0", inclusive = true, message = "La longitud debe estar entre -180 y 180")
        Double longitude,

        @NotNull(message = "El tipo de suelo es obligatorio")
        SoilType soilType,

        @NotBlank(message = "El responsable (ownerOrLead) es obligatorio")
        @Size(min = 3, max = 80, message = "El responsable debe tener entre 3 y 80 caracteres")
        String ownerOrLead
) {
}
