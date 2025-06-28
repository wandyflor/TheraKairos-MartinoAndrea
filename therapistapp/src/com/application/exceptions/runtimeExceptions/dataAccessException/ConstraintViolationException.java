package com.application.exceptions.runtimeExceptions.dataAccessException;

/**
 * Cuando hay violaciones de restricciones únicas (ej: duplicados)
 */
public class ConstraintViolationException extends DataAccessException {
    private final String entityName;
    private final String field;

    /**
     * Constructor de la excepción.
     *
     * @param entityName     nombre de la entidad donde ocurrió la violación
     * @param constraintName nombre de la restricción (campo) violada
     */
    public ConstraintViolationException(String entityName, String constraintName) {
        super(String.format("Violación de restricción '%s' en entidad '%s'", constraintName, entityName));
        this.entityName = entityName;
        this.field = constraintName;
    }

    /**
     * Obtiene el nombre de la entidad donde ocurrió la violación.
     *
     * @return nombre de la entidad
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * Obtiene el campo (constraint) que fue violado.
     *
     * @return nombre de la restricción violada
     */
    public String getField() {
        return field;
    }
}
