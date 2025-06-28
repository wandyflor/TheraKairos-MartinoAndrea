package com.application.interfaces;

public interface IConsultationPatientActionsEvent {
    
    public void onIsPaid(String patientId);

    public void onDelete(String patientId);

}
