package com.application.interfaces;

import com.application.exceptions.businessException.BusinessException;
import com.application.exceptions.businessException.ValidationException;
import com.application.model.dto.ConsultationDTO;
import com.application.model.dto.ConsultationPatientDTO;
import com.application.model.dto.PatientDTO;
import java.io.IOException;
import java.util.List;

public interface IConsultationDialog {
    
    ConsultationDTO getConsultationById(String consultationId); 
    
    List<ConsultationPatientDTO> getConsultationPatientsByConsultationId(String consultationId);
    
    void insertConsultationWithPatients(
            ConsultationDTO consultationDTO, 
            List<ConsultationPatientDTO> consultationPatientsDTO) throws ValidationException, BusinessException, IOException;
    
    void updateConsultationWithPatients(
            ConsultationDTO consultationDTO, 
            List<ConsultationPatientDTO> consultationPatientsDTO) throws ValidationException, BusinessException, IOException;
    
    List<PatientDTO> getAllPatients();
    
    PatientDTO getPatientById(String patientId);
    
    void openConsultationNotesById(String consultationId);
      
}

