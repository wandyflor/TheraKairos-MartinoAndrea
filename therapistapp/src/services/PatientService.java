package com.application.services;

import com.application.exceptions.businessException.BusinessException;
import com.application.exceptions.businessException.ValidationException;
import com.application.exceptions.runtimeExceptions.dataAccessException.ConstraintViolationException;
import com.application.exceptions.runtimeExceptions.dataAccessException.DataAccessException;
import com.application.exceptions.runtimeExceptions.dataAccessException.EntityNotFoundException;
import com.application.model.dao.PatientDAO;
import com.application.model.dto.PatientDTO;
import com.application.model.entities.Patient;
import com.application.utils.PatientsFilesManager;
import java.io.IOException;
import java.nio.file.Path;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PatientService {
    private final PatientDAO patientDAO;
    private final PatientsFilesManager fileManager;
    
    // Patron simple para validar e-mail (puede ajustarse si se requiere más estricto)
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");

    // Patron simple para teléfono (solo dígitos, opcional “+” al inicio, 7–15 dígitos)
    private static final Pattern PHONE_PATTERN =
        Pattern.compile("^\\+?\\d{7,15}$");
    
    public PatientService() {
        this.patientDAO = new PatientDAO();
        this.fileManager = new PatientsFilesManager(); 
    }
    
    /**
     * Obtiene todos los pacientes convertidos a DTO
     * @return Lista de PatientDTO
     * @throws BusinessException Si ocurre un error al acceder a los datos
     */
    public List<PatientDTO> getAllPatients() throws BusinessException {
        try {
            return patientDAO.getAllPatients().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            throw new BusinessException("Error al listar pacientes", e);
        }
    }

    /**
     * Inserta un nuevo paciente
     * @param patientDTO Datos del paciente a insertar
     * @throws ValidationException Si los datos no son válidos o el paciente ya existe
     * @throws BusinessException Si ocurre otro error de negocio
     * @throws IOException
     */
    public void insertPatient(PatientDTO patientDTO) throws ValidationException, BusinessException, IOException {
        try {
            validatePatientData(patientDTO);
            Patient patient = createPatientFromDTO(patientDTO, false);
            patientDAO.insertPatient(patient);
            fileManager.initPatientFolders(patient.getPatientId());
            movePatientPhotoIfExists(patientDTO, patient.getPatientId());
        } catch (ConstraintViolationException e) {
            throw new ValidationException("Ya existe un paciente con ese " + e.getField());
        } catch (DataAccessException e) {
            throw new BusinessException("Error al insertar paciente", e);
        }
    }
    
    /**
     * Modifica un paciente existente
     * @param patientDTO Datos del paciente a modificar
     * @throws ValidationException Si los datos no son válidos o el paciente ya existe
     * @throws BusinessException Si ocurre otro error de negocio
     * @throws IOException
     */
    public void updatePatient(PatientDTO patientDTO) throws ValidationException, BusinessException, IOException {       
        try {
            validatePatientData(patientDTO);
            Patient patient = createPatientFromDTO(patientDTO, true);
            patientDAO.updatePatient(patient);
            managePatientPhoto(patientDTO, patient.getPatientId());
        } catch (EntityNotFoundException e) {
            throw new ValidationException("No existe paciente con Id '" + patientDTO.getPatientDTOId() + "'");
        } catch (ConstraintViolationException e) {
            throw new ValidationException("Ya existe otro paciente con ese " + e.getField());
        } catch (DataAccessException e) {
            throw new BusinessException("Error al actualizar paciente", e);
        }
    }
    
    /**
     * Elimina un paciente existente en el sistema
     * @param patientId del paciente a eliminar
     * @throws ValidationException Si los datos no son válidos o el paciente no existe
     * @throws BusinessException Si ocurre un error durante el proceso
     */
    public void deletePatient(String patientId) throws ValidationException, BusinessException {
        try {
            patientDAO.deletePatient(UUID.fromString(patientId));
        } catch (EntityNotFoundException e) {
            throw new ValidationException("No existe paciente con Id '" + patientId + "'");
        } catch (DataAccessException e) {
            throw new BusinessException("Error al eliminar paciente", e);
        }
    }

    /**
     * Obtiene el paciente en base a un dni
     * @param patientId del paciente a buscar
     * @return PatientDTO 
     * @throws ValidationException Si los datos no son válidos o el paciente no existe
     * @throws BusinessException Si ocurre un error durante el proceso
     */
    public PatientDTO getPatientById(String patientId) throws ValidationException, BusinessException {
        try {
            return convertToDTO(patientDAO.getPatientById(UUID.fromString(patientId)));
        } catch (EntityNotFoundException e) {
            throw new ValidationException("No existe paciente con Id '" + patientId + "'");
        } catch (DataAccessException e) {
            throw new BusinessException("Error al buscar paciente", e);
        }
    }
    
    /**
     * Valida los datos del paciente antes de la inserción
     */
    private void validatePatientData(PatientDTO dto) throws ValidationException {
        if (dto.getPatientDTODNI() == null || !dto.getPatientDTODNI().matches("\\d{8}")) {
            throw new ValidationException("El DNI debe contener 8 dígitos");
        }
        if (isNullOrEmpty(dto.getPatientDTOName())) {
            throw new ValidationException("El nombre es requerido");
        }
        if (isNullOrEmpty(dto.getPatientDTOLastName())) {
            throw new ValidationException("El apellido es requerido");
        }
        if (isNullOrEmpty(dto.getPatientDTOBirthDate())) {
            throw new ValidationException("La fecha de nacimiento es requerida");
        }
        try {
            LocalDate birth = LocalDate.parse(dto.getPatientDTOBirthDate());
            if (birth.isAfter(LocalDate.now())) {
                throw new ValidationException("La fecha de nacimiento no puede ser futura");
            }
        } catch (Exception e) {
            throw new ValidationException("Formato de fecha inválido (YYYY-MM-DD)");
        }
        if (isNullOrEmpty(dto.getPatienDTOOccupation())) {
            throw new ValidationException("La ocupación es requerida");
        }
        if (!PHONE_PATTERN.matcher(dto.getPatientDTOPhone()).matches()) {
            throw new ValidationException("Formato de teléfono inválido");
        }
        if (!EMAIL_PATTERN.matcher(dto.getPatientDTOEmail()).matches()) {
            throw new ValidationException("Formato de email inválido");
        }
        if (isNullOrEmpty(dto.getCityId())) {
            throw new ValidationException("La ciudad es requerida");
        }
        if (isNullOrEmpty(dto.getPatientDTOAddress())) {
            throw new ValidationException("La dirección es requerida");
        }
        if (isNullOrEmpty(dto.getPatientDTOAddressNumber()) || !dto.getPatientDTOAddressNumber().matches("\\d+")) {
            throw new ValidationException("El número de dirección debe ser numérico");
        }
        String floor = dto.getPatientDTOAddressFloor();
        if (floor != null && !floor.isEmpty() && !floor.matches("\\d+")) {
            throw new ValidationException("El piso debe ser numérico");
        }
    }
    
    private void movePatientPhotoIfExists(PatientDTO dto, UUID patientId) throws IOException {
        if (!dto.getPatientDTOPhotoPath().isEmpty()) {
            fileManager.copyPhotoToPatientDir(patientId, Path.of(dto.getPatientDTOPhotoPath()));
        }
    }

    /**
     * Obtiene el paciente en base a un dni
     * @param patientDTO del paciente
     * @param patientId Identificador del paciente 
     * @throws BusinessException Si ocurre un error durante el proceso
     */
    private void managePatientPhoto(PatientDTO dto, UUID patientId) throws IOException {
        boolean newPhoto = !fileManager.hasPatientPhoto(patientId) && !dto.getPatientDTOPhotoPath().isEmpty();
        boolean changedPhoto = fileManager.hasPatientPhoto(patientId) && !dto.getPatientDTOPhotoPath().isEmpty() && (!fileManager.getPatientPhotoPath(patientId).equals(dto.getPatientDTOPhotoPath()));
        boolean deletedPhoto = fileManager.hasPatientPhoto(patientId) && dto.getPatientDTOPhotoPath().isEmpty();
        
        if (newPhoto || changedPhoto) {
            fileManager.copyPhotoToPatientDir(patientId, Path.of(dto.getPatientDTOPhotoPath()));
        } 
        
        if (deletedPhoto) {
            fileManager.deletePatientPhoto(patientId);
        }

    }

    /**
     * Crea un objeto Patient a partir de un PatientDTO
     */
    private Patient createPatientFromDTO(PatientDTO dto, boolean isUpdate) {
        return new Patient(
                isUpdate ? UUID.fromString(dto.getPatientDTOId()) : UUID.randomUUID(),
                dto.getPatientDTODNI().trim(),
                dto.getPatientDTOName().trim().toLowerCase(),
                dto.getPatientDTOLastName().trim().toLowerCase(),
                LocalDate.parse(dto.getPatientDTOBirthDate().trim()),
                dto.getPatienDTOOccupation().trim().toLowerCase(),
                dto.getPatientDTOPhone().trim(),
                dto.getPatientDTOEmail().trim().toLowerCase(),
                UUID.fromString(dto.getCityId().trim()),
                dto.getPatientDTOAddress().trim().toLowerCase(),
                Integer.parseInt(dto.getPatientDTOAddressNumber().trim()),
                dto.getPatientDTOAddressFloor() != null && !dto.getPatientDTOAddressFloor().isEmpty()
                        ? Integer.parseInt(dto.getPatientDTOAddressFloor().trim()) : 0,
                dto.getPatientDTOAddressDepartment() != null ? dto.getPatientDTOAddressDepartment().trim().toLowerCase() : null
        );
    }

    /**
     * Crea un objeto PatientDTO a partir de un Patient
     */
    private PatientDTO convertToDTO(Patient p) {
        PatientDTO dto = new PatientDTO();
        dto.setPatientDTOId(p.getPatientId().toString());
        dto.setPatientDTODNI(p.getPatientDNI());
        dto.setPatientDTOName(p.getPatientName());
        dto.setPatientDTOLastName(p.getPatientLastName());
        dto.setPatientDTOBirthDate(p.getPatientBirthDate().toString());
        dto.setPatientDTOOccupation(p.getPatientOccupation());
        dto.setPatientDTOPhone(p.getPatientPhone());
        dto.setPatientDTOEmail(p.getPatientEmail());
        dto.setCityId(p.getCityId().toString());
        dto.setPatientDTOAddress(p.getPatientAddress());
        dto.setPatientDTOAddressNumber(String.valueOf(p.getPatientAddressNumber()));
        dto.setPatientDTOAddressFloor(p.getPatientAddressFloor() > 0 ? String.valueOf(p.getPatientAddressFloor()) : "");
        dto.setPatientDTOAddressDepartment(p.getPatientAddressDepartment() != null ? p.getPatientAddressDepartment() : "");
        try {
            dto.setPatientDTOPhotoPath(fileManager.hasPatientPhoto(p.getPatientId()) ? fileManager.getPatientPhoto(p.getPatientId()).toString() : "");
        } catch (IOException e) {
            dto.setPatientDTOPhotoPath("");
        }
        return dto;
    }
    
    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }    
}