package com.application.exceptions.runtimeExceptions.presentationException;

/**
 * Cuando falla la validación de datos en el cliente
 */
public class ClientValidationException extends PresentationException {
    private final String fieldName;
    
    public ClientValidationException(String fieldName, String message) {
        super(message);
        this.fieldName = fieldName;
    }
    
    public String getFieldName() {
        return fieldName;
    }
}
