package com.application.controllers.entities;

import com.application.services.CityService;
import com.application.services.ConsultationService;
import com.application.services.PatientService;

public class ControllerRegistry {
    private final ConsultationController consultationController;
    private final PatientController patientController;
    private final CityController cityController;

    public ControllerRegistry() {
        this.consultationController = new ConsultationController(new ConsultationService());
        this.patientController = new PatientController(new PatientService());
        this.cityController = new CityController(new CityService());
    }

    public ConsultationController getConsultationController() {
        return consultationController;
    }

    public PatientController getPatientController() {
        return patientController;
    }

    public CityController getCityController() {
        return cityController;
    }
}

