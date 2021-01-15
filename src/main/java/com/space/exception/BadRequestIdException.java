package com.space.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestIdException extends RuntimeException{
    public BadRequestIdException() {
    }

    public BadRequestIdException(String message) {
        super(message);
    }
}
