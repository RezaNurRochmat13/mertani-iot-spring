package com.mertani.iot.exception;

public class DuplicateSensorIdException extends RuntimeException {
    public DuplicateSensorIdException(String message) {
        super(message);
    }
}
