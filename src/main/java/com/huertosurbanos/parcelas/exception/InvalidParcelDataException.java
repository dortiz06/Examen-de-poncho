package com.huertosurbanos.parcelas.exception;

import org.springframework.http.HttpStatus;

public class InvalidParcelDataException extends ApiException {

    public InvalidParcelDataException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
