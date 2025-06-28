package com.application.interfaces;

import com.application.model.dto.PatientDTO;
import java.util.List;

public interface IConsultationPatientsDialog {
    
    List<PatientDTO> getAllPatients();
    
    void updateConsultationPatientsDTO(List<String> patientsId);
    
}
