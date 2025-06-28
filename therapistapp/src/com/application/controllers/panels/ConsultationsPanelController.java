package com.application.controllers.panels;

import com.application.controllers.entities.ControllerRegistry;
import com.application.exceptions.businessException.BusinessException;
import com.application.exceptions.businessException.ValidationException;
import com.application.model.dto.CityDTO;
import com.application.model.dto.ConsultationDTO;
import com.application.model.dto.ConsultationPatientDTO;
import com.application.model.dto.PatientDTO;
import com.application.view.panels.consultation.ConsultationsPanel;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ConsultationsPanelController {
    
    private final ControllerRegistry controllerRegistry;
    private final ConsultationsPanel consultationsForm;
    
    public ConsultationsPanelController(ControllerRegistry controllerRegistry) {
        this.controllerRegistry = controllerRegistry;
        
        this.consultationsForm = new ConsultationsPanel();
        this.consultationsForm.setController(this);
    }
    
    public ConsultationsPanel getView() {
        return consultationsForm;
    }
          
    /**
     * Inserta una nueva consulta en el sistema
     * @param consultationDTO Datos de la consulta a insertar
     * @param consultationPatientsDTO Datos de los pacientes
     * @throws ValidationException Si los datos no son válidos o la consulta ya existe
     * @throws BusinessException Si ocurre un error durante el proceso
     * @throws java.io.IOException
     */
    public void insertConsultationWithPatients(
            ConsultationDTO consultationDTO, 
            List<ConsultationPatientDTO> consultationPatientsDTO) throws ValidationException, BusinessException, IOException {
        controllerRegistry.getConsultationController()
                .insertConsultationWithPatients(consultationDTO, consultationPatientsDTO);
    }
    
    /**
     * Modifica una consulta existente en el sistema
     * @param consultationDTO Datos de la consulta a modificar
     * @param consultationPatientsDTO Datos de los pacientes
     * @throws ValidationException Si los datos no son válidos o la consulta ya existe
     * @throws BusinessException Si ocurre un error durante el proceso
     */
    public void updateConsultationWithPatients(
            ConsultationDTO consultationDTO, 
            List<ConsultationPatientDTO> consultationPatientsDTO) throws ValidationException, BusinessException {
        controllerRegistry.getConsultationController()
                .updateConsultationWithPatients(consultationDTO, consultationPatientsDTO);
    }
    
    /**
     * Elimina una consulta existente en el sistema
     * @param consultationId de la consulta a eliminar
     */
    public void deleteConsultation(String consultationId) {
        try {
            controllerRegistry.getConsultationController().deleteConsultation(consultationId);
        } catch (ValidationException e) {
            consultationsForm.showErrorMessage("Validación: " + e.getMessage());
        } catch (BusinessException e) {
            consultationsForm.showErrorMessage("Error: " + e.getMessage());
        } catch (IOException e) {
            consultationsForm.showErrorMessage("Error: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene una consulta para un Identificador determinado
     * @param consultationId Identificador de la consulta a buscar
     * @return DTO de la consulta
     */
    public ConsultationDTO getConsultationById(String consultationId)  {
        try {
            return controllerRegistry.getConsultationController().getConsultationById(consultationId);
        } catch (ValidationException e) {
            consultationsForm.showErrorMessage("Validación: " + e.getMessage());
            return null;
        } catch (BusinessException e) {
            consultationsForm.showErrorMessage("Error al obtener consulta: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Obtiene las consulta para un dia determinado
     * @param consultationDate fecha de las consultas a buscar
     * @return lista de DTOs de consulta para la fecha especificada
     */
    public List<ConsultationDTO> getConsultationsByDate(String consultationDate) {
        try {
            return controllerRegistry.getConsultationController().getConsultationsByDate(consultationDate);
        } catch (ValidationException e) {
            consultationsForm.showErrorMessage("Validación: " + e.getMessage());
            return List.of();
        } catch (BusinessException e) {
            consultationsForm.showErrorMessage("Error al obtener consultas por fecha: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Obtiene todos los pacientes 
     * @return lista de DTO's de los pacientes
     */
    public List<PatientDTO> getAllPatients() {
        try {
            return controllerRegistry.getPatientController().getAllPatients();
        } catch (BusinessException e) {
            consultationsForm.showErrorMessage("Error al obtener pacientes: " + e.getMessage());
            return List.of();
        }
    }
        
    /**
     * Obtiene los nombres de los pacientes asociados a una consulta determinada
     * @param consultationId Identificador de la consulta
     * @return lista de DTO's de los pacientes asociados a la consulta
     */
    public List<ConsultationPatientDTO> getPatientsByConsultationId(String consultationId) {
        try {
            return controllerRegistry.getPatientController()
                .getPatientsByConsultationId(consultationId);
        } catch (ValidationException e) {
            consultationsForm.showErrorMessage("Validación: " + e.getMessage());
            return List.of();
        } catch (BusinessException e) {
            consultationsForm.showErrorMessage("Error al obtener pacientes de la consulta: " + e.getMessage());
            return List.of();
        }
    }
          
    /**
     * Obtiene el paciente en base a su Id
     * @param patientId
     * @return PatientDTO
     */
    public PatientDTO getPatientById(String patientId) {
        try {
            return controllerRegistry.getPatientController().getPatientById(patientId);
        } catch (ValidationException | BusinessException e) {
            consultationsForm.showErrorMessage(e.getMessage());
            return null;
        }
    } 
    
    /**
     * Abre las notas asociadas a la consulta
     * @param consultationId Identificador de la consulta
     */
    public void openConsultationNotesById(String consultationId) {
        try {
            controllerRegistry.getConsultationController().openConsultationNotesById(consultationId);
        } catch (ValidationException | BusinessException e) {
            consultationsForm.showErrorMessage(e.getMessage());
        } catch (IOException ex) {
            consultationsForm.showErrorMessage(ex.getMessage());
        }
    } 
    
    /**
     * Obtiene todas las ciudades en el sistema
     * @return List CityDTO
     */
    public List<CityDTO> getAllCities() {
        try {
            List<CityDTO> cities = controllerRegistry.getCityController().getAllCities();
            return cities != null ? cities : Collections.emptyList();
        } catch (BusinessException e) {
            consultationsForm.showErrorMessage("Error al obtener ciudades: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Inserta un nuevo paciente
     * @param patientDTO Datos del paciente a insertar
     * @throws com.application.exceptions.businessException.ValidationException
     * @throws com.application.exceptions.businessException.BusinessException
     * @throws java.io.IOException
     */
    public void insertPatient(PatientDTO patientDTO) throws ValidationException, BusinessException, IOException {
        controllerRegistry.getPatientController().insertPatient(patientDTO);
    }
    
    /**
     * Modifica paciente existente
     * @param patientDTO Datos del paciente a modificar
     * @throws com.application.exceptions.businessException.ValidationException
     * @throws com.application.exceptions.businessException.BusinessException
     * @throws java.io.IOException
     */
    public void updatePatient(PatientDTO patientDTO) throws ValidationException, BusinessException, IOException {
        controllerRegistry.getPatientController().updatePatient(patientDTO);
    }
}