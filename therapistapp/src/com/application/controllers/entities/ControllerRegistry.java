package com.application.controllers.entities;

import com.application.services.CityService;
import com.application.services.ConsultationPatientService;
import com.application.services.ConsultationService;
import com.application.services.PatientService;

public class ControllerRegistry {
    private final ConsultationController consultationController;
    private final ConsultationPatientController consultationPatientController;
    private final PatientController patientController;
    private final CityController cityController;

    public ControllerRegistry() {
        this.consultationController = new ConsultationController(new ConsultationService());
        this.consultationPatientController = new ConsultationPatientController(new ConsultationPatientService());
        this.patientController = new PatientController(new PatientService());
        this.cityController = new CityController(new CityService());
    }

    public ConsultationController getConsultationController() {
        return consultationController;
    }

    public ConsultationPatientController getConsultationPatientController() {
        return consultationPatientController;
    }

    public PatientController getPatientController() {
        return patientController;
    }

    public CityController getCityController() {
        return cityController;
    }
}

