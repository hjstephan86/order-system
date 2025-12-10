package com.example.exception;

public class EntityNotFoundException extends RuntimeException {

    public static final long serialVersionUID = 1L;

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String entityType, Long id) {
        super(String.format("%s mit ID %d nicht gefunden", entityType, id));
    }
}
