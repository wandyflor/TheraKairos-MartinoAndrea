package com.application.interfaces;

import com.application.exceptions.businessException.BusinessException;
import com.application.exceptions.businessException.ValidationException;
import com.application.model.dto.ConsultationDTO;
import com.application.model.dto.PatientDTO;
import java.util.List;

public interface IConsultationDialogListener {
    
    ConsultationDTO getConsultationById(String consultationId); 
    
    List<PatientDTO> getPatientsByConsultationId(String consultationId);
    
    void insertConsultation(ConsultationDTO consultationDTO) throws ValidationException, BusinessException;
    
    void updateConsultation(ConsultationDTO consultationDTO) throws ValidationException, BusinessException;
    
    PatientDTO getPatientById(String patientId);
    
    void deletePatientConsultation(String consultationId, String patientId);
    
}

