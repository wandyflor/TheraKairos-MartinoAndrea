package com.application.exceptions.runtimeExceptions.dataAccessException;

/**
 * Cuando no se encuentra un recurso en la base de datos
 */
public class EntityNotFoundException extends DataAccessException {
    public EntityNotFoundException(String entityName, Object id) {
        super(String.format("%s con ID %s no encontrado", entityName, id.toString()));
    }
}