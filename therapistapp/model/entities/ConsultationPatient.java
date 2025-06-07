package com.application.model.entities;

import java.util.UUID;

public class ConsultationPatient {
    private UUID consultationId;
    private UUID patientId;
    private Boolean isPaid;
    private String patientNotePath;

    public ConsultationPatient(
            UUID consultationId, 
            UUID patientId, 
            Boolean isPaid, 
            String patientNotePath) {
        this.consultationId = consultationId;
        this.patientId = patientId;
        this.isPaid = isPaid;
        this.patientNotePath = patientNotePath;
    }

    public UUID getConsultationId() {
        return consultationId;
    }

    public void setConsultationId(UUID consultationId) {
        this.consultationId = consultationId;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public void setPatientId(UUID patientId) {
        this.patientId = patientId;
    }

    public Boolean getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(Boolean isPaid) {
        this.isPaid = isPaid;
    }

    public String getPatientNotePath() {
        return patientNotePath;
    }

    public void setPatientNotePath(String patientNotePath) {
        this.patientNotePath = patientNotePath;
    }    
}
