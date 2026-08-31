package com.finflow.shared;

/** Basisklasse für "Ressource nicht gefunden" über alle Module hinweg (siehe GlobalExceptionHandler). */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
