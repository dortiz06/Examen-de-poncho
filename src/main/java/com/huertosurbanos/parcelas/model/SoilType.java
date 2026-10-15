package com.huertosurbanos.parcelas.model;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.huertosurbanos.parcelas.config.SoilTypeDeserializer;

@JsonDeserialize(using = SoilTypeDeserializer.class)
public enum SoilType {
    ARCILLOSO,
    LIMOSO,
    ARENOSO;

    @JsonValue
    public String getValue() {
        return name();
    }
}
