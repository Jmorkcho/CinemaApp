package com.finals.cinema.util.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)

public class BadGetawayException extends RuntimeException {

    public BadGetawayException(String message) {
        super(message);
    }
}
