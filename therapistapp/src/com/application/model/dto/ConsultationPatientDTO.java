package com.application.model.dto;

public class ConsultationPatientDTO {
    private String consultationId;
    private String patientId;
    private String isPaid;

    public ConsultationPatientDTO() {
    }

    public ConsultationPatientDTO(
            String consultationId, 
            String patientId, 
            String isPaid) {
        this.consultationId = consultationId;
        this.patientId = patientId;
        this.isPaid = isPaid;
    }
    
    public String getConsultationId() {
        return consultationId;
    }

    public void setConsultationId(String consultationId) {
        this.consultationId = consultationId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(String isPaid) {
        this.isPaid = isPaid;
    }
  
}
