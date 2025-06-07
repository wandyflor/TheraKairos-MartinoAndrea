package com.application.controllers.panels;

import com.application.controllers.entities.CityController;
import com.application.controllers.entities.ControllerRegistry;
import com.application.controllers.entities.PatientController;
import com.application.exceptions.businessException.BusinessException;
import com.application.exceptions.businessException.ValidationException;
import com.application.model.dto.CityDTO;
import com.application.model.dto.PatientDTO;
import com.application.view.panels.patient.PatientsPanel;
import java.io.IOException;
import java.util.List;
import java.util.Collections;

public class PatientsPanelController {
    
    private final ControllerRegistry controllerRegistry;
    private final PatientsPanel patientsForm;
    
    public PatientsPanelController(ControllerRegistry controllerRegistry) {
        this.controllerRegistry = controllerRegistry;

        this.patientsForm = new PatientsPanel();
        this.patientsForm.setController(this);
    }
    
    public PatientsPanel getView() {
        return patientsForm;
    }
    
    /**
     * Obtiene todos los pacientes en el sistema
     * @return List PatientDTO
     */
    public List<PatientDTO> getAllPatients() {
        try {
            List<PatientDTO> patients = controllerRegistry.getPatientController().getAllPatients();
            return patients != null ? patients : Collections.emptyList();
        } catch (BusinessException e) {
            patientsForm.showErrorMessage("Error al obtener pacientes: " + e.getMessage());
            return Collections.emptyList();
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
            patientsForm.showErrorMessage("Error al obtener ciudades: " + e.getMessage());
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
    
    /**
     * Elimina paciente existente
     * @param patientId del paciente a eliminar
     */
    public void deletePatient(String patientId) {
        try {
            controllerRegistry.getPatientController().deletePatient(patientId);
        } catch (ValidationException | BusinessException e) {
            patientsForm.showErrorMessage(e.getMessage());
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
            patientsForm.showErrorMessage(e.getMessage());
            return null;
        }
    } 
    
    /**
     * Obtiene el nombre de la ciudad en base a su Id
     * @param cityId
     * @return String City name
     */
    public String getCityNameById(String cityId) {
        try {
            return controllerRegistry.getCityController().getCityNameById(cityId);
        } catch (ValidationException | BusinessException e) {
            patientsForm.showErrorMessage(e.getMessage());
            return null;
        }
    }
    
    /**
     * Busca pacientes en base a su apellido
     * @param patientData Search term
     * @return List PatientDTO 
     */
    public List<PatientDTO> getPatientsThatMatch(String patientData) {
        try {
            List<PatientDTO> result = controllerRegistry.getPatientController().getPatientsThatMatch(patientData);
            return result != null ? result : Collections.emptyList();
        } catch (BusinessException e) {
            patientsForm.showErrorMessage("Error en búsqueda: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}