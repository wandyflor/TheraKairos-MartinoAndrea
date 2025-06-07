package com.application.exceptions.runtimeExceptions.dataAccessException;

/**
 * Excepción base para todos los errores de acceso a datos
 * Incluye causa original para logging técnico
 */
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DataAccessException(String message) {
        super(message);
    }
}
