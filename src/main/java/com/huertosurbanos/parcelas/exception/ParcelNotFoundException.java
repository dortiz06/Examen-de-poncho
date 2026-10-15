package com.huertosurbanos.parcelas.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class ParcelNotFoundException extends ApiException {

    public ParcelNotFoundException(UUID id) {
        super(HttpStatus.NOT_FOUND, "No se encontró la parcela con id " + id);
    }
}
