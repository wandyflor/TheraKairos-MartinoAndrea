package com.application.exceptions.runtimeExceptions.presentationException;

/**
 * Para errores específicos de la interfaz de usuario
 */
public class PresentationException extends RuntimeException {
    public PresentationException(String message) {
        super(message);
    }
}
