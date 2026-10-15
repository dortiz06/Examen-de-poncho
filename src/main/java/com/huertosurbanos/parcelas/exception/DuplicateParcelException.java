package com.huertosurbanos.parcelas.exception;

import org.springframework.http.HttpStatus;

public class DuplicateParcelException extends ApiException {

    public DuplicateParcelException(String name, String ownerOrLead) {
        super(
                HttpStatus.CONFLICT,
                "Ya existe una parcela con el nombre '" + name + "' y el responsable '" + ownerOrLead + "'"
        );
    }
}
