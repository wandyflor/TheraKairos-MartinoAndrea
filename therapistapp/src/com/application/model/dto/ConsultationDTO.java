package com.application.model.dto;

public class ConsultationDTO {
    private String consultationDTOId;
    private String consultationDTODate;
    private String consultationDTOStartTime;
    private String consultationDTOEndTime;
    private String consultationDTOAmount;
    private String consultationDTOStatus;

    public ConsultationDTO() {
    }

    public ConsultationDTO(
            String consultationDTOId, 
            String consultationDTODate, 
            String consultationDTOStartTime, 
            String consultationDTOEndTime, 
            String consultationDTOAmount, 
            String consultationDTOStatus) {
        this.consultationDTOId = consultationDTOId;
        this.consultationDTODate = consultationDTODate;
        this.consultationDTOStartTime = consultationDTOStartTime;
        this.consultationDTOEndTime = consultationDTOEndTime;
        this.consultationDTOAmount = consultationDTOAmount;
        this.consultationDTOStatus = consultationDTOStatus;
    }

    public String getConsultationDTOId() {
        return consultationDTOId;
    }

    public void setConsultationDTOId(String consultationDTOId) {
        this.consultationDTOId = consultationDTOId;
    }

    public String getConsultationDTODate() {
        return consultationDTODate;
    }

    public void setConsultationDTODate(String consultationDTODate) {
        this.consultationDTODate = consultationDTODate;
    }

    public String getConsultationDTOStartTime() {
        return consultationDTOStartTime;
    }

    public void setConsultationDTOStartTime(String consultationDTOStartTime) {
        this.consultationDTOStartTime = consultationDTOStartTime;
    }

    public String getConsultationDTOEndTime() {
        return consultationDTOEndTime;
    }

    public void setConsultationDTOEndTime(String consultationDTOEndTime) {
        this.consultationDTOEndTime = consultationDTOEndTime;
    }

    public String getConsultationDTOAmount() {
        return consultationDTOAmount;
    }

    public void setConsultationDTOAmount(String consultationDTOAmount) {
        this.consultationDTOAmount = consultationDTOAmount;
    }

    public String getConsultationDTOStatus() {
        return consultationDTOStatus;
    }

    public void setConsultationDTOStatus(String consultationDTOStatus) {
        this.consultationDTOStatus = consultationDTOStatus;
    }
}
