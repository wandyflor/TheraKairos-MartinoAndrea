package com.application.interfaces;

public interface IPanelMessages {
    
    void showInformationMessage(String message);
    
    void showErrorMessage(String message);
    
    Boolean showConfirmAction(String message);
    
}
