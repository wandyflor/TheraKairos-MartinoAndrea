package com.application.model.entities;

import com.application.model.enumerations.ConsultationStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class Consultation {
    private UUID consultationId;
    private LocalDate consultationDate;
    private LocalTime consultationStartTime;
    private LocalTime consultationEndTime;
    private Double consultationAmount;
    private ConsultationStatus consultationStatus;

    public Consultation(
            UUID consultationId, 
            LocalDate consultationDate, 
            LocalTime consultationStartTime, 
            LocalTime consultationEndTime, 
            Double consultationAmount, 
            ConsultationStatus consultationStatus) {
        this.consultationId = consultationId;
        this.consultationDate = consultationDate;
        this.consultationStartTime = consultationStartTime;
        this.consultationEndTime = consultationEndTime;
        this.consultationAmount = consultationAmount;
        this.consultationStatus = consultationStatus;
    }

    public UUID getConsultationId() {
        return consultationId;
    }

    public void setConsultationId(UUID consultationId) {
        this.consultationId = consultationId;
    }

    public LocalDate getConsultationDate() {
        return consultationDate;
    }

    public void setConsultationDate(LocalDate consultationDate) {
        this.consultationDate = consultationDate;
    }

    public LocalTime getConsultationStartTime() {
        return consultationStartTime;
    }

    public void setConsultationStartTime(LocalTime consultationStartTime) {
        this.consultationStartTime = consultationStartTime;
    }

    public LocalTime getConsultationEndTime() {
        return consultationEndTime;
    }

    public void setConsultationEndTime(LocalTime consultationEndTime) {
        this.consultationEndTime = consultationEndTime;
    }

    public Double getConsultationAmount() {
        return consultationAmount;
    }

    public void setConsultationAmount(Double consultationAmount) {
        this.consultationAmount = consultationAmount;
    }

    public ConsultationStatus getConsultationStatus() {
        return consultationStatus;
    }

    public void setConsultationStatus(ConsultationStatus consultationStatus) {
        this.consultationStatus = consultationStatus;
    }
}
