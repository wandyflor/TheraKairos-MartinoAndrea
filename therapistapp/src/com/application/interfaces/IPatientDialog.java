package com.application.interfaces;

import com.application.exceptions.businessException.BusinessException;
import com.application.exceptions.businessException.ValidationException;
import com.application.model.dto.CityDTO;
import com.application.model.dto.PatientDTO;
import java.io.IOException;
import java.util.List;

public interface IPatientDialog {
    
    PatientDTO getPatientById(String patientId); 
    
    List<CityDTO> getAllCities(); 
    
    void insertPatient(PatientDTO patientDTO) throws ValidationException, BusinessException, IOException;
    
    void updatePatient(PatientDTO patientDTO) throws ValidationException, BusinessException, IOException;
    
}

