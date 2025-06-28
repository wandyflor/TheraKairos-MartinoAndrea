package com.application.exceptions.businessException;

/**
 * Excepción base para todos los errores de negocio
 */
public class BusinessException extends Exception {
    public BusinessException(String message) {
        super(message);
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
