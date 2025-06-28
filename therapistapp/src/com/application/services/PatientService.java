package com.application.services;

import com.application.exceptions.businessException.BusinessException;
import com.application.exceptions.businessException.ValidationException;
import com.application.exceptions.runtimeExceptions.dataAccessException.ConstraintViolationException;
import com.application.exceptions.runtimeExceptions.dataAccessException.DataAccessException;
import com.application.exceptions.runtimeExceptions.dataAccessException.EntityNotFoundException;
import com.application.model.dao.ConsultationPatientDAO;
import com.application.model.dao.PatientDAO;
import com.application.model.dto.ConsultationPatientDTO;
import com.application.model.dto.PatientDTO;
import com.application.model.entities.ConsultationPatient;
import com.application.model.entities.Patient;
import com.application.utils.PatientsFilesManager;
import java.io.IOException;
import java.nio.file.Path;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PatientService {
    private final PatientDAO patientDAO;
    private final ConsultationPatientDAO consultationPatientDAO;
    private final PatientsFilesManager fileManager;
    
    // Patron simple para validar e-mail (puede ajustarse si se requiere más estricto)
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");

    // Patron simple para teléfono (solo dígitos, opcional “+” al inicio, 7–15 dígitos)
    private static final Pattern PHONE_PATTERN =
        Pattern.compile("^\\+?\\d{7,15}$");
    
    public PatientService() {
        this.patientDAO = new PatientDAO();
        this.consultationPatientDAO = new ConsultationPatientDAO();
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
                    .map(this::createPatientDTOFromPatient)
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
            Patient patient = createPatientFromPatientDTO(patientDTO);
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
            Patient patient = createPatientFromPatientDTO(patientDTO);
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
            UUID patientUUID = UUID.fromString(patientId);
            patientDAO.deletePatient(patientUUID);
            consultationPatientDAO.deletePatientFromAllConsultation(patientUUID);
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
            Patient patient = patientDAO.getPatientById(UUID.fromString(patientId));
            return createPatientDTOFromPatient(patient);
        } catch (EntityNotFoundException e) {
            throw new ValidationException("No existe paciente con Id '" + patientId + "'");
        } catch (DataAccessException e) {
            throw new BusinessException("Error al buscar paciente", e);
        }
    }
    
    /**
     * Obtiene los pacientes de una consulta determinada
     * @param consultationId
     * @return Lista de PatientDTO
     * @throws BusinessException Si ocurre un error al acceder a los datos
     */
    public List<ConsultationPatientDTO> getPatientsByConsultationId(String consultationId) throws BusinessException {
        try {
            UUID cId = UUID.fromString(consultationId);
            return consultationPatientDAO.getPatientsByConsultationId(cId)
                .stream().map(this::createConsultationPatientDTOFromConsultationPatient)
                .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Id de consulta mal formado", e);
        } catch (DataAccessException e) {
            throw new BusinessException("Error al listar pacientes", e);
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
    private Patient createPatientFromPatientDTO(PatientDTO patientDTO) {
        UUID patientId = Optional.ofNullable(patientDTO.getPatientDTOId())
                .filter(s -> !s.isBlank())
                .map(UUID::fromString)
                .orElseGet(UUID::randomUUID);
        
        return new Patient(
                patientId,
                patientDTO.getPatientDTODNI().trim(),
                patientDTO.getPatientDTOName().trim().toLowerCase(),
                patientDTO.getPatientDTOLastName().trim().toLowerCase(),
                LocalDate.parse(patientDTO.getPatientDTOBirthDate().trim()),
                patientDTO.getPatienDTOOccupation().trim().toLowerCase(),
                patientDTO.getPatientDTOPhone().trim(),
                patientDTO.getPatientDTOEmail().trim().toLowerCase(),
                UUID.fromString(patientDTO.getCityId().trim()),
                patientDTO.getPatientDTOAddress().trim().toLowerCase(),
                Integer.parseInt(patientDTO.getPatientDTOAddressNumber().trim()),
                patientDTO.getPatientDTOAddressFloor() != null && !patientDTO.getPatientDTOAddressFloor().isEmpty()
                        ? Integer.parseInt(patientDTO.getPatientDTOAddressFloor().trim()) : 0,
                patientDTO.getPatientDTOAddressDepartment() != null ? patientDTO.getPatientDTOAddressDepartment().trim().toLowerCase() : null
        );
    }

    /**
     * Crea un objeto PatientDTO a partir de un Patient
     */
    private PatientDTO createPatientDTOFromPatient(Patient patient) {
        PatientDTO dto = new PatientDTO();
        dto.setPatientDTOId(patient.getPatientId().toString());
        dto.setPatientDTODNI(patient.getPatientDNI());
        dto.setPatientDTOName(patient.getPatientName());
        dto.setPatientDTOLastName(patient.getPatientLastName());
        dto.setPatientDTOBirthDate(patient.getPatientBirthDate().toString());
        dto.setPatientDTOOccupation(patient.getPatientOccupation());
        dto.setPatientDTOPhone(patient.getPatientPhone());
        dto.setPatientDTOEmail(patient.getPatientEmail());
        dto.setCityId(patient.getCityId().toString());
        dto.setPatientDTOAddress(patient.getPatientAddress());
        dto.setPatientDTOAddressNumber(String.valueOf(patient.getPatientAddressNumber()));
        dto.setPatientDTOAddressFloor(patient.getPatientAddressFloor() > 0 ? String.valueOf(patient.getPatientAddressFloor()) : "");
        dto.setPatientDTOAddressDepartment(patient.getPatientAddressDepartment() != null ? patient.getPatientAddressDepartment() : "");
        try {
            dto.setPatientDTOPhotoPath(fileManager.hasPatientPhoto(patient.getPatientId()) ? fileManager.getPatientPhoto(patient.getPatientId()).toString() : "");
        } catch (IOException e) {
            dto.setPatientDTOPhotoPath("");
        }
        return dto;
    }
    
    /**
     * Crea un objeto ConsultationPatient a partir de un ConsultationPatientDTO
     */
    private ConsultationPatient createConsultationPatientFromConsultationPatientDTO(ConsultationPatientDTO dto) {
        return new ConsultationPatient(
            UUID.fromString(dto.getConsultationId()),
            UUID.fromString(dto.getPatientId()),
            Boolean.valueOf(dto.getIsPaid())
        );
    }
    
    /**
     * Crea un objeto ConsultationPatientDTO a partir de un ConsultationPatient
     */
    private ConsultationPatientDTO createConsultationPatientDTOFromConsultationPatient(ConsultationPatient cp) {
        ConsultationPatientDTO dto = new ConsultationPatientDTO();
        dto.setConsultationId(cp.getConsultationId().toString());
        dto.setPatientId(cp.getPatientId().toString());
        dto.setIsPaid(cp.getIsPaid().toString());
        
        return dto;
    }
    
    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }    
}