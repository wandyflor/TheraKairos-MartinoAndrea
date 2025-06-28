package com.application.model.entities;

import java.util.UUID;

public class ConsultationPatient {
    private UUID consultationId;
    private UUID patientId;
    private Boolean isPaid;

    public ConsultationPatient(
            UUID consultationId, 
            UUID patientId, 
            Boolean isPaid) {
        this.consultationId = consultationId;
        this.patientId = patientId;
        this.isPaid = isPaid;
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
  
}
