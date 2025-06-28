package com.application.interfaces;

public interface IConsultationActionsEvent {

    public void onEdit(String patientId);

    public void onDelete(String patientId);

    public void onView(String patientId);
}
