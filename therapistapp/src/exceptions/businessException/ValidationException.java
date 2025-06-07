package com.application.exceptions.businessException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Excepción que indica que hubo uno o más errores de validación
 * sobre los datos de entrada.  
 * Contiene un mensaje general y un Map<campo, mensaje> con detalle.
 */
public class ValidationException extends Exception {
    private final Map<String, String> errors;

    /**
     * Excepción con mensaje global y sin detalles por campo.
     * @param message mensaje global de la excepción
     */
    public ValidationException(String message) {
        super(message);
        this.errors = Collections.emptyMap();
    }

    /**
     * Excepción con detalle para un solo campo.
     * @param field   nombre del campo con error
     * @param message mensaje de error específico para ese campo
     */
    public ValidationException(String field, String message) {
        super(message);
        this.errors = Collections.unmodifiableMap(
            Collections.singletonMap(field, message)
        );
    }

    /**
     * Excepción con mensaje global y mapa de errores por campo.
     * @param message mensaje global de la excepción
     * @param errors  mapa campo→mensaje, puede ser null o vacío
     */
    public ValidationException(String message, Map<String, String> errors) {
        super(message);
        if (errors == null || errors.isEmpty()) {
            this.errors = Collections.emptyMap();
        } else {
            // copia defensiva e inmutable
            this.errors = Collections.unmodifiableMap(new HashMap<>(errors));
        }
    }
    
    /**
    * Excepción con mensaje global y causa encadenada.
    * @param message mensaje global de la excepción
    * @param cause excepción original que causó esta
    */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
        this.errors = Collections.emptyMap();
    }
    
    /**
     * @return Mapa (campo→mensaje) con los errores detallados; nunca null.
     */
    public Map<String, String> getErrors() {
        return errors;
    }
}
