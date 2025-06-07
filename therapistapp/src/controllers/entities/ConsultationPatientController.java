package com.application.controllers.entities;

import com.application.exceptions.businessException.BusinessException;
import com.application.exceptions.businessException.ValidationException;
import com.application.model.dto.ConsultationPatientDTO;
import com.application.model.dto.PatientDTO;

import java.util.List;

import com.application.services.ConsultationPatientService;

public class ConsultationPatientController {
    private final ConsultationPatientService consultationPatientService;
    
    public ConsultationPatientController(ConsultationPatientService consultationPatientService) {
        this.consultationPatientService = consultationPatientService;
    }
    
    /**
     * Inserta un nuevo paciente en una consulta existente en el sistema
     * @param consultationPatientDTO Datos del paciente a insertar
     * @throws ValidationException Si los datos no son válidos o la consulta no existe
     * @throws BusinessException Si ocurre un error durante el proceso
     */
    public void insertConsultationPatient(ConsultationPatientDTO consultationPatientDTO) throws ValidationException, BusinessException {
        validateBasicFields(consultationPatientDTO);
        consultationPatientService.insertConsultationPatient(consultationPatientDTO);
    }
    
    /**
     * Elimina al paciente de la consulta existente en el sistema
     * @param consultationId Identificador de la consulta
     * @param patientId Identificador del paciente
     * @throws ValidationException Si los datos no son válidos o la consulta no existe
     * @throws BusinessException Si ocurre un error durante el proceso
     */
    public void deleteConsultationPatient(String consultationId, String patientId) throws ValidationException, BusinessException {
        if (consultationId == null || consultationId.trim().isEmpty()) {
            throw new ValidationException("El Identificador de la consulta es requerido");
        }
        if (patientId == null || patientId.trim().isEmpty()) {
            throw new ValidationException("El Identificador del paciente es requerido");
        }
        consultationPatientService.deleteConsultationPatient(consultationId, patientId);
    }
    
    /**
     * Obtiene los pacientes de una consulta determinada
     * @param consultationId Identificador de la consulta
     * @return Lista de PatientDTO
     * @throws ValidationException Si los datos no son válidos o la consulta no existe
     * @throws BusinessException Si ocurre un error durante el proceso
     */
    public List<PatientDTO> getPatientsByConsultationId(String consultationId) throws ValidationException, BusinessException {
        if (consultationId == null || consultationId.trim().isEmpty()) {
            throw new ValidationException("El Identificador de la consulta es requerido");
        }
        return consultationPatientService.getPatientsByConsultationId(consultationId);
    }
    
    /**
     * Modifica el estado del pago de consulta para el paciente indicado
     * @param consultationId Identificador de la consulta
     * @param patientId Identificador del paciente
     * @throws ValidationException Si los datos no son válidos o la consulta no existe
     * @throws BusinessException Si ocurre un error durante el proceso
     */
    public void setConsultationPatientPaid(String consultationId, String patientId) throws ValidationException, BusinessException {
        if (consultationId == null || consultationId.trim().isEmpty()) {
            throw new ValidationException("El Identificador de la consulta es requerido");
        }
        if (patientId == null || patientId.trim().isEmpty()) {
            throw new ValidationException("El Identificador del paciente es requerido");
        }
        consultationPatientService.setConsultationPatientPaid(consultationId, patientId);
    }

    /**
     * Valida los datos del paciente de la consulta
     * @param consultationPatientDTO datos de; paciente de la consulta a validar
     * @throws ValidationException si la validacion falla
     */
    private void validateBasicFields(ConsultationPatientDTO consultationPatientDTO) throws ValidationException {
        if (consultationPatientDTO == null) {
            throw new ValidationException("Los datos son requeridos");
        }
        if (consultationPatientDTO.getIsPaid()== null || consultationPatientDTO.getIsPaid().trim().isEmpty()) {
            throw new ValidationException("El estado del pago de la consulta es requerido");
        }
        if (consultationPatientDTO.getPatientNotePath() == null || consultationPatientDTO.getPatientNotePath().trim().isEmpty()) {
            throw new ValidationException("El path de las notas de paciente para la consulta es requerido");
        }
    }
}