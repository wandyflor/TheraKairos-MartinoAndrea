package com.application.view.panels.patient;

public interface IPatientActionsEvent {

    public void onEdit(String patientId);

    public void onDelete(String patientId);

    public void onView(String patientId);
}
