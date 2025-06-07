package com.application.exceptions.businessException;

/**
 * Para problemas de concurrencia (ej: optimistic locking)
 */
public class ConcurrencyException extends BusinessException {
    public ConcurrencyException(String entityName) {
        super(String.format("Conflicto de concurrencia al actualizar %s", entityName));
    }
}
