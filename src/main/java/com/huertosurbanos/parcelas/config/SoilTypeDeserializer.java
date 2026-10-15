package com.huertosurbanos.parcelas.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.huertosurbanos.parcelas.model.SoilType;
import com.huertosurbanos.parcelas.util.InputSanitizer;

import java.io.IOException;

public class SoilTypeDeserializer extends JsonDeserializer<SoilType> {

    private final InputSanitizer inputSanitizer = new InputSanitizer();

    @Override
    public SoilType deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String raw = parser.getValueAsString();
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String normalized = inputSanitizer.normalizeSoilType(raw);
        try {
            return SoilType.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw InvalidFormatException.from(
                    parser,
                    "El tipo de suelo '" + raw + "' no es válido. Valores permitidos: ARCILLOSO, LIMOSO, ARENOSO",
                    raw,
                    SoilType.class
            );
        }
    }
}
