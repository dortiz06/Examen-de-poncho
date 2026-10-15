package com.huertosurbanos.parcelas.util;

import com.huertosurbanos.parcelas.dto.ParcelRequest;
import com.huertosurbanos.parcelas.dto.ParcelResponse;
import com.huertosurbanos.parcelas.exception.InvalidParcelDataException;
import com.huertosurbanos.parcelas.model.Parcel;
import com.huertosurbanos.parcelas.model.SoilType;
import org.springframework.stereotype.Component;

@Component
public class ParcelMapper {

    private final InputSanitizer inputSanitizer;

    public ParcelMapper(InputSanitizer inputSanitizer) {
        this.inputSanitizer = inputSanitizer;
    }

    public Parcel toEntity(ParcelRequest request) {
        String name = inputSanitizer.sanitizeText(request.name());
        String ownerOrLead = inputSanitizer.sanitizeText(request.ownerOrLead());
        validateSanitizedText("name", name);
        validateSanitizedText("ownerOrLead", ownerOrLead);

        String normalizedSoilType = inputSanitizer.normalizeSoilType(
                request.soilType() == null ? null : request.soilType().name()
        );
        SoilType soilType = parseSoilType(normalizedSoilType);

        Parcel parcel = new Parcel();
        parcel.setName(name);
        parcel.setLatitude(request.latitude());
        parcel.setLongitude(request.longitude());
        parcel.setSoilType(soilType);
        parcel.setOwnerOrLead(ownerOrLead);
        return parcel;
    }

    public ParcelResponse toResponse(Parcel parcel) {
        return new ParcelResponse(
                parcel.getId(),
                parcel.getName(),
                parcel.getLatitude(),
                parcel.getLongitude(),
                parcel.getSoilType(),
                parcel.getOwnerOrLead(),
                parcel.getCreatedAt()
        );
    }

    private void validateSanitizedText(String field, String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidParcelDataException(
                    "El campo " + field + " quedó vacío después de sanitizar el contenido"
            );
        }
        if (value.length() < 3 || value.length() > 80) {
            throw new InvalidParcelDataException(
                    "El campo " + field + " debe tener entre 3 y 80 caracteres después de sanitizar"
            );
        }
    }

    private SoilType parseSoilType(String normalized) {
        if (normalized == null || normalized.isBlank()) {
            throw new InvalidParcelDataException("El tipo de suelo es obligatorio");
        }
        try {
            return SoilType.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw new InvalidParcelDataException(
                    "El tipo de suelo '" + normalized + "' no es válido. Valores permitidos: ARCILLOSO, LIMOSO, ARENOSO"
            );
        }
    }
}
