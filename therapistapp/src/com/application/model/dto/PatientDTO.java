package com.application.model.dto;

import java.awt.Image;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class PatientDTO {
    private String patientDTOId;
    private String patientDTODNI;
    private String patientDTOName;
    private String patientDTOLastName;
    private String patientDTOBirthDate;
    private String patientDTOOccupation;
    private String patientDTOPhone;
    private String patientDTOEmail;
    private String cityId;
    private String patientDTOAddress;
    private String patientDTOAddressNumber;
    private String patientDTOAddressFloor;
    private String patientDTOAddressDepartment;
    // Manejar la imagen asociada al paciente
    private String patientDTOPhotoPath;

    private static final String DEFAULT_PHOTO_PATH = "C:\\Users\\nsalazar\\Documents\\therapistapp\\appdata\\default_photo_user.jpg"; 

    public PatientDTO() {
    }
    
    public PatientDTO(
            String patientDTOId, 
            String patientDTODNI, 
            String patientDTOName, 
            String patientDTOLastName, 
            String patientDTOBirthDate, 
            String patientDTOOccupation, 
            String patientDTOPhone, 
            String patientDTOEmail, 
            String cityId, 
            String patientDTOAddress, 
            String patientDTOAddressNumber, 
            String patientDTOAddressFloor, 
            String patientDTOAddressDepartment,
            String patientDTOPhotoPath) {
        this.patientDTOId = patientDTOId;
        this.patientDTODNI = patientDTODNI;
        this.patientDTOName = patientDTOName;
        this.patientDTOLastName = patientDTOLastName;
        this.patientDTOBirthDate = patientDTOBirthDate;
        this.patientDTOOccupation = patientDTOOccupation;
        this.patientDTOPhone = patientDTOPhone;
        this.patientDTOEmail = patientDTOEmail;
        this.cityId = cityId;
        this.patientDTOAddress = patientDTOAddress;
        this.patientDTOAddressNumber = patientDTOAddressNumber;
        this.patientDTOAddressFloor = patientDTOAddressFloor;
        this.patientDTOAddressDepartment = patientDTOAddressDepartment;
        this.patientDTOPhotoPath = patientDTOPhotoPath;
    }

    public String getPatientDTOId() {
        return patientDTOId;
    }

    public void setPatientDTOId(String patientDTOId) {
        this.patientDTOId = patientDTOId;
    }

    public String getPatientDTODNI() {
        return patientDTODNI;
    }

    public void setPatientDTODNI(String patientDTODNI) {
        this.patientDTODNI = patientDTODNI;
    }

    public String getPatientDTOName() {
        return patientDTOName;
    }

    public void setPatientDTOName(String patientDTOName) {
        this.patientDTOName = patientDTOName;
    }

    public String getPatientDTOLastName() {
        return patientDTOLastName;
    }

    public void setPatientDTOLastName(String patientDTOLastName) {
        this.patientDTOLastName = patientDTOLastName;
    }

    public String getPatientDTOBirthDate() {
        return patientDTOBirthDate;
    }

    public void setPatientDTOBirthDate(String patientDTOBirthDate) {
        this.patientDTOBirthDate = patientDTOBirthDate;
    }
    
    public String getPatienDTOOccupation() {
        return patientDTOOccupation;
    }

    public void setPatientDTOOccupation(String patientDTOOccupation) {
        this.patientDTOOccupation = patientDTOOccupation;
    }

    public String getPatientDTOPhone() {
        return patientDTOPhone;
    }

    public void setPatientDTOPhone(String patientDTOPhone) {
        this.patientDTOPhone = patientDTOPhone;
    }

    public String getPatientDTOEmail() {
        return patientDTOEmail;
    }

    public void setPatientDTOEmail(String patientDTOEmail) {
        this.patientDTOEmail = patientDTOEmail;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getPatientDTOAddress() {
        return patientDTOAddress;
    }

    public void setPatientDTOAddress(String patientDTOAddress) {
        this.patientDTOAddress = patientDTOAddress;
    }

    public String getPatientDTOAddressNumber() {
        return patientDTOAddressNumber;
    }

    public void setPatientDTOAddressNumber(String patientDTOAddressNumber) {
        this.patientDTOAddressNumber = patientDTOAddressNumber;
    }

    public String getPatientDTOAddressFloor() {
        return patientDTOAddressFloor;
    }

    public void setPatientDTOAddressFloor(String patientDTOAddressFloor) {
        this.patientDTOAddressFloor = patientDTOAddressFloor;
    }

    public String getPatientDTOAddressDepartment() {
        return patientDTOAddressDepartment;
    }

    public void setPatientDTOAddressDepartment(String patientDTOAddressDepartment) {
        this.patientDTOAddressDepartment = patientDTOAddressDepartment;
    }

    public String getPatientDTOPhotoPath() {
        return patientDTOPhotoPath;
    }

    public void setPatientDTOPhotoPath(String patientDTOPhotoPath) {
        this.patientDTOPhotoPath = patientDTOPhotoPath;
    }  
    
    public Icon getPatientDTOIcon() {
        ImageIcon original;
        if (!patientDTOPhotoPath.isEmpty()) {
            original = new ImageIcon(getPatientDTOPhotoPath());
        } else {
            original = new ImageIcon(DEFAULT_PHOTO_PATH);
        }

        ImageIcon icon = new ImageIcon(original.getImage()
                .getScaledInstance(88, 88, Image.SCALE_SMOOTH));
        
        return icon;
    }
    
    public String getPatientDTOActualAge() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Period age = Period.between(
                LocalDate.parse(patientDTOBirthDate, format), 
                LocalDate.now()
        );
        return String.valueOf(age.getYears());
    }
    
    public String getPatientDTOFormattedCompleteName() {
        return patientDTOLastName + ", " + patientDTOName;
    }
        
    @Override
    public String toString() {
        return patientDTOLastName + ", " + patientDTOName; 
    }
}
