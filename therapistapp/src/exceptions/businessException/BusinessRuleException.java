package com.application.exceptions.businessException;

/**
 * Cuando una operación no está permitida por reglas de negocio
 */
public class BusinessRuleException extends BusinessException {
    public BusinessRuleException(String rule, String message) {
        super(String.format("Regla de negocio violada [%s]: %s", rule, message));
    }
}
