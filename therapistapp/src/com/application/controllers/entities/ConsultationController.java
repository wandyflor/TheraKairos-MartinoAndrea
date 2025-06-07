package com.application.controllers.entities;

import com.application.exceptions.businessException.BusinessException;
import com.application.exceptions.businessException.ValidationException;
import com.application.model.dto.ConsultationDTO;
import com.application.services.ConsultationService;

import java.util.List;
import java.util.UUID;

public class ConsultationController {
    private final ConsultationService consultationService;
    
    public ConsultationController(ConsultationService consultationService) {
        this.consultationService = consultationService;
    }
    
    /**
     * Inserta una nueva consulta en el sistema
     * @param consultationDTO Datos de la consulta a insertar
     * @throws ValidationException Si los datos no son válidos o la consulta ya existe
     * @throws BusinessException Si ocurre un error durante el proceso
     */
    public void insertConsultation(ConsultationDTO consultationDTO) throws ValidationException, BusinessException {
        validateBasicFields(consultationDTO);
        consultationService.insertConsultation(consultationDTO);
    }
    
    /**
     * Modifica una consulta existente en el sistema
     * @param consultationDTO Datos de la consulta a modificar
     * @throws ValidationException Si los datos no son válidos o la consulta ya existe
     * @throws BusinessException Si ocurre un error durante el proceso
     */
    public void updateConsultation(ConsultationDTO consultationDTO) throws ValidationException, BusinessException {
        validateBasicFields(consultationDTO);
        consultationService.updateConsultation(consultationDTO);
    }
    
    /**
     * Elimina una consulta existente en el sistema
     * @param consultationId de la consulta a eliminar
     * @throws ValidationException Si los datos no son válidos o la consulta no existe
     * @throws BusinessException Si ocurre un error durante el proceso
     */
    public void deleteConsultation(String consultationId) throws ValidationException, BusinessException {
        if (consultationId == null || consultationId.trim().isEmpty()) {
            throw new ValidationException("El Identificador de la consulta es requerido");
        }
        consultationService.deleteConsultation(consultationId);
    }
    
    /**
     * Obtiene la consulta para un identificador determinado
     * @param consultationId Identificador de la consulta a buscar
     * @return DTO de la consulta 
     * @throws ValidationException  Si los datos no son válidos o la consulta no existe
     * @throws BusinessException Si ocurre un error durante el proceso
     */
    public ConsultationDTO getConsultationById(String consultationId) throws ValidationException, BusinessException {
        if (consultationId == null || consultationId.trim().isEmpty()) {
            throw new ValidationException("El Identificador de la consulta es requerido");
        }
        return consultationService.getConsultationById(consultationId);
    }
    
    /**
     * Obtiene las consulta para un dia determinado
     * @param consultationDate fecha de las consultas a buscar
     * @return lista de DTOs de consulta para la fecha especificada
     * @throws ValidationException  Si los datos no son válidos o la consulta no existe
     * @throws BusinessException Si ocurre un error durante el proceso
     */
    public List<ConsultationDTO> getConsultationsByDate(String consultationDate) throws ValidationException, BusinessException {
        if (consultationDate == null || consultationDate.trim().isEmpty()) {
            throw new ValidationException("La fecha de la consulta es requerida");
        }
        return consultationService.getConsultationsByDate(consultationDate).stream().toList();
    }
        
    /**
     * Valida los datos estructurales mínimos de la consulta
     * @param consultationDTO datos de la consulta a validar
     * @throws ValidationException si algún dato obligatorio es inválido
     */
    public void validateBasicFields(ConsultationDTO consultationDTO) throws ValidationException {
        if (consultationDTO == null) {
            throw new ValidationException("Los datos de la consulta no pueden ser nulos");
        }

        if (isBlank(consultationDTO.getConsultationDTODate())) {
            throw new ValidationException("La fecha de la consulta es requerida");
        }
        
        if (isBlank(consultationDTO.getConsultationDTOStartTime())) {
            throw new ValidationException("El horario de inicio de la consulta es requerido");
        }

        if (isBlank(consultationDTO.getConsultationDTOEndTime())) {
            throw new ValidationException("El horario de fin de la consulta es requerido");
        }

        if (isBlank(consultationDTO.getConsultationDTOAmount())) {
            throw new ValidationException("El monto de la consulta es requerido");
        }

        // Validar si el monto es un número válido
        try {
            Double.valueOf(consultationDTO.getConsultationDTOAmount());
        } catch (NumberFormatException e) {
            throw new ValidationException("El monto de la consulta debe ser un número válido");
        }

        if (isBlank(consultationDTO.getConsultationDTOStatus())) {
            throw new ValidationException("El estado de la consulta es requerido");
        }

        // Validar formato del ID si existe
        if (consultationDTO.getConsultationDTOId() != null && !consultationDTO.getConsultationDTOId().isBlank()) {
            try {
                UUID.fromString(consultationDTO.getConsultationDTOId());
            } catch (IllegalArgumentException e) {
                throw new ValidationException("El ID de la consulta tiene un formato inválido");
            }
        }
    }

    /**
     * Retorna true si la cadena es null o está vacía después de trim().
     */
    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}