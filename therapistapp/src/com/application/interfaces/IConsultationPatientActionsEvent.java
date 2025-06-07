package com.application.interfaces;

public interface IConsultationPatientActionsEvent {

    public void onDelete(String patientId);

    public void onView(String patientId);
    
}
