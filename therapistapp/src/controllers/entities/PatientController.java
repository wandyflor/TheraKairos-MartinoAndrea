package com.application.controllers.entities;

import com.application.exceptions.businessException.BusinessException;
import com.application.exceptions.businessException.ValidationException;
import com.application.model.dto.PatientDTO;
import com.application.services.PatientService;
import java.io.IOException;

import java.util.List;

public class PatientController {
    private final PatientService patientService;
    
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }
 
    /**
     * Retrieves all patients from the system
     * @return List of PatientDTO objects
     * @throws BusinessException If there's an error accessing data
     */
    public List<PatientDTO> getAllPatients() throws BusinessException {
        return patientService.getAllPatients();
    }

    /**
     * Inserta un nuevo paciente
     * @param patientDTO Datos del paciente a insertar
     * @throws ValidationException Si los datos no son válidos o el paciente ya existe
     * @throws BusinessException Si ocurre otro error de negocio
     * @throws java.io.IOException
     */
    public void insertPatient(PatientDTO patientDTO) throws ValidationException, BusinessException, IOException {
        validateBasicFields(patientDTO);
        patientService.insertPatient(patientDTO);
    }
    
    /**
     * Modifica paciente existente
     * @param patientDTO Datos del paciente a modificar
     * @throws ValidationException Si los datos no son válidos
     * @throws BusinessException Si ocurre otro error de negocio
     * @throws java.io.IOException
     */
    public void updatePatient(PatientDTO patientDTO) throws ValidationException, BusinessException, IOException {
        validateBasicFields(patientDTO);
        patientService.updatePatient(patientDTO);
    }
    
    /**
     * Elimina paciente existente
     * @param patientId del paciente a eliminar
     * @throws ValidationException Si los datos no son válidos o el paciente ya existe
     * @throws BusinessException Si ocurre otro error de negocio
     */
    public void deletePatient(String patientId) throws ValidationException, BusinessException {
        if (patientId == null || patientId.trim().isEmpty()) {
            throw new ValidationException("El Id del paciente es requerido");
        }
        patientService.deletePatient(patientId);
    }

    /**
     * Obtiene el paciente en base a su Id
     * @param patientId del paciente a buscar
     * @return PatientDTO si lo encuentra
     * @throws ValidationException Si los datos no son válidos o el paciente ya existe
     * @throws BusinessException Si ocurre otro error de negocio
     */
    public PatientDTO getPatientById(String patientId) throws ValidationException, BusinessException {
        if (patientId == null || patientId.trim().isEmpty()) {
            throw new ValidationException("El Id del paciente es requerido");
        }
        return patientService.getPatientById(patientId);
    }
    
    /**
     * Busca pacientes en base a su apellido o su nombre
     * @param patientData Search term
     * @return Lista de pacientes que coincidan
     * @throws BusinessException  Si ocurre otro error de negocio
     */
    public List<PatientDTO> getPatientsThatMatch(String patientData) throws BusinessException {
        String term = patientData != null ? patientData.toLowerCase().trim() : "";
        return patientService.getAllPatients().stream()
            .filter(p -> p.getPatientDTOLastName().toLowerCase().contains(term)
                     || p.getPatientDTOName().toLowerCase().contains(term))
            .toList();
    }
    
    /**
     * Valida los datos del paciente
     * @param patientDTO datos del paciente a validar
     * @throws ValidationException si la validacion falla
     */
    private void validateBasicFields(PatientDTO dto) throws ValidationException {
        if (dto == null) {
            throw new ValidationException("Los datos del paciente son requeridos");
        }
        if (dto.getPatientDTODNI() == null || dto.getPatientDTODNI().trim().isEmpty()) {
            throw new ValidationException("El DNI del paciente es requerido");
        }
        if (dto.getPatientDTOName() == null || dto.getPatientDTOName().trim().isEmpty()) {
            throw new ValidationException("El nombre del paciente es requerido");
        }
        if (dto.getPatientDTOLastName() == null || dto.getPatientDTOLastName().trim().isEmpty()) {
            throw new ValidationException("El apellido del paciente es requerido");
        }
        if (dto.getPatientDTOBirthDate() == null || dto.getPatientDTOBirthDate().trim().isEmpty()) {
            throw new ValidationException("La fecha de nacimiento es requerida");
        }
        if (dto.getPatientDTOPhone() == null || dto.getPatientDTOPhone().trim().isEmpty()) {
            throw new ValidationException("El número de teléfono del paciente es requerido");
        }
        if (dto.getPatientDTOEmail() == null || dto.getPatientDTOEmail().trim().isEmpty()) {
            throw new ValidationException("El email del paciente es requerido");
        }
        if (dto.getCityId() == null || dto.getCityId().trim().isEmpty()) {
            throw new ValidationException("La ciudad del paciente es requerida");
        }
        if (dto.getPatientDTOAddress() == null || dto.getPatientDTOAddress().trim().isEmpty()) {
            throw new ValidationException("La dirección del paciente es requerida");
        }
        if (dto.getPatientDTOAddressNumber() == null || dto.getPatientDTOAddressNumber().trim().isEmpty()) {
            throw new ValidationException("El número de dirección del paciente es requerido");
        }
    }
}
