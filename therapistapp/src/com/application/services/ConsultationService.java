package com.application.services;

import com.application.exceptions.businessException.BusinessException;
import com.application.exceptions.businessException.ValidationException;
import com.application.exceptions.runtimeExceptions.dataAccessException.DataAccessException;
import com.application.exceptions.runtimeExceptions.dataAccessException.EntityNotFoundException;
import com.application.model.dao.ConsultationDAO;
import com.application.model.dao.ConsultationPatientDAO;
import com.application.model.dto.ConsultationDTO;
import com.application.model.dto.ConsultationPatientDTO;
import com.application.model.dto.PatientDTO;
import com.application.model.entities.Consultation;
import com.application.model.entities.ConsultationPatient;
import com.application.model.enumerations.ConsultationStatus;
import com.application.utils.ConsultationsFilesManager;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class ConsultationService {
    private final ConsultationDAO consultationDAO;
    private final ConsultationPatientDAO consultationPatientDAO;
    private final ConsultationsFilesManager fileManager;

    public ConsultationService() {
        this.consultationDAO = new ConsultationDAO();
        this.consultationPatientDAO = new ConsultationPatientDAO();
        this.fileManager = new ConsultationsFilesManager(); 
    }
    
    /**
     * Inserta una nueva consulta y los pacientes asociados en el sistema
     * @param consultationDTO Datos de la consulta a insertar
     * @param consultationPatientsDTO Lista de pacientes
     * @throws ValidationException Si los datos no son válidos o la consulta ya existe
     * @throws BusinessException Si ocurre un error durante el proceso
     * @throws java.io.IOException
     */
    public void insertConsultationWithPatients(
            ConsultationDTO consultationDTO, 
            List<ConsultationPatientDTO> consultationPatientsDTO) throws ValidationException, BusinessException, IOException {
        try {
            
            validateConsultationData(consultationDTO);
            Consultation consultation = createConsultationFromConsultationDTO(consultationDTO);
            consultationDAO.insertConsultation(consultation);
            
            for(ConsultationPatientDTO consultationPatientDTO: consultationPatientsDTO) {
                ConsultationPatient consultationPatient = createConsultationPatientFromConsultationPatientDTO(consultationPatientDTO);
                consultationPatientDAO.insertConsultationPatient(consultationPatient);
            }
            
            fileManager.initConsultationFolders(consultation.getConsultationId());
            fileManager.createNotesFile(consultation.getConsultationId());

        } catch (DataAccessException e) {
            throw new BusinessException("Error al guardar la consulta en el sistema", e);
        } catch (IllegalArgumentException e) {
           throw new ValidationException("Formato de fecha inválido");
        }
    }
    
    /**
     * Modifica una consulta existente y los pacientes asociados en el sistema
     * @param consultationDTO Datos de la consulta a modificar
     * @param consultationPatientsDTO Lista de pacientes
     * @throws ValidationException Si los datos no son válidos o la consulta ya existe
     * @throws BusinessException Si ocurre un error durante el proceso
     */
    public void updateConsultationWithPatients(
            ConsultationDTO consultationDTO,
            List<ConsultationPatientDTO> consultationPatientsDTO) throws ValidationException, BusinessException {
        try {
            
            validateConsultationData(consultationDTO);
            Consultation consultation = createConsultationFromConsultationDTO(consultationDTO);
            consultationDAO.updateConsultation(consultation);
            
            UUID consultationId = consultation.getConsultationId();

            // Pacientes activos actuales
            List<UUID> existingPatientIds = consultationPatientDAO.getPatientsIdByConsultationId(consultationId);

            // Pacientes recibidos en la nueva lista
            List<UUID> newPatientIds = consultationPatientsDTO.stream()
                    .map(cpdto -> UUID.fromString(cpdto.getPatientId()))
                    .collect(Collectors.toList());

            // 1. Insertar o reactivar pacientes nuevos
            for (ConsultationPatientDTO cpdto : consultationPatientsDTO) {
                UUID patientId = UUID.fromString(cpdto.getPatientId());

                if (!existingPatientIds.contains(patientId)) {
                    // Verificar si el paciente estaba borrado lógicamente
                    if (consultationPatientDAO.existsInactiveConsultationPatient(consultationId, patientId)) {
                        consultationPatientDAO.reactivateConsultationPatient(consultationId, patientId, Boolean.parseBoolean(cpdto.getIsPaid()));
                    } else {
                        ConsultationPatient cp = createConsultationPatientFromConsultationPatientDTO(cpdto);
                        consultationPatientDAO.insertConsultationPatient(cp);
                    }
                } else {
                    // Ya existe, pero puede haber cambiado el estado de pago
                    if (Boolean.parseBoolean(cpdto.getIsPaid())) {
                        consultationPatientDAO.setConsultationPatientIsPaid(consultationId, patientId);
                    }
                }
            }

            // 2. Eliminar pacientes que ya no están en la nueva lista
            for (UUID existingId : existingPatientIds) {
                if (!newPatientIds.contains(existingId)) {
                    consultationPatientDAO.deleteConsultationPatient(consultationId, existingId);
                }
            }

        } catch (DataAccessException e) {
            throw new BusinessException("Error al guardar la consulta en el sistema", e);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Formato de fecha inválido");
        }
    }
    
    /**
     * Elimina una consulta existente en el sistema
     * @param consultationId de la consulta a eliminar
     * @throws ValidationException Si los datos no son válidos o la consulta no existe
     * @throws BusinessException Si ocurre un error durante el proceso
     * @throws java.io.IOException
     */
    public void deleteConsultationWithPatients(String consultationId) throws ValidationException, BusinessException, IOException {
        try {
            
            UUID consultationUUID = UUID.fromString(consultationId);
            
            consultationDAO.deleteConsultation(consultationUUID);
            
            consultationPatientDAO.deleteAllConsultationPatients(consultationUUID);
            
            fileManager.deleteConsultationFolder(consultationUUID);
            
        } catch (EntityNotFoundException e) {
            throw new ValidationException("No existe consulta con Id '" + consultationId + "'");
        } catch (DataAccessException e) {
            throw new BusinessException("Error de base de datos al eliminar la consulta", e);
        }
    }
    
    /**
     * Obtiene la consulta para un identificador determinado
     * @param consultationId Identificador de la consulta a buscar
     * @return DTO de consulta 
     * @throws BusinessException Si ocurre un error durante el proceso
     */
    public ConsultationDTO getConsultationById(String consultationId) throws BusinessException {
        try {
            
            Consultation consultation = consultationDAO.getConsultationById(UUID.fromString(consultationId));
            
            return createConsultationDTOFromConsultation(consultation);
            
        } catch (DataAccessException e) {
            throw new BusinessException("Error al listar consultas", e);
        }
    }
    
    /**
     * Obtiene las consulta para un dia determinado
     * @param consultationDate fecha de las consultas a buscar
     * @return lista de DTOs de consulta para la fecha especificada
     * @throws BusinessException Si ocurre un error durante el proceso
     */
   public List<ConsultationDTO> getConsultationsByDate(String consultationDate) throws BusinessException {
        try {
            
            java.sql.Date sqlDate = java.sql.Date.valueOf(consultationDate);

            return consultationDAO
                    .getConsultationsByDate(sqlDate)
                    .stream()
                    .map(this::createConsultationDTOFromConsultation)
                    .collect(Collectors.toList());

        } catch (DateTimeParseException e) {
            throw new BusinessException("Formato de fecha inválido, debe ser yyyy-MM-dd", e);
        } catch (DataAccessException e) {
            throw new BusinessException("Error al listar consultas por fecha", e);
        }
    }
    
    /**
     * Abre las notas asociadas a una consulta
     * @param consultationId Identificador de la consulta
     * @throws ValidationException Si los datos no son válidos o la consulta no existe
     * @throws BusinessException Si ocurre un error durante el proceso
     * @throws java.io.IOException
     */
   public void openConsultationNotesById(String consultationId) throws ValidationException, BusinessException, IOException {
        try {
            
            UUID consultationUUID = UUID.fromString(consultationId);
            
            fileManager.openConsultationNotesFile(consultationUUID);
            
        } catch (EntityNotFoundException e) {
            throw new ValidationException("No existe consulta con Id '" + consultationId + "'");
        } catch (DataAccessException e) {
            throw new BusinessException("Error de base de datos al eliminar la consulta", e);
        }
    }
   
    /**
     * Valida los datos de formato y de negocio de la consulta
     * @param consultationDTO datos de la consulta a validar
     * @throws ValidationException si algún dato obligatorio es inválido
     */
    private void validateConsultationData(ConsultationDTO consultationDTO) throws ValidationException {
        LocalDate consultationDate;
        LocalTime consultationStartTime;
        LocalTime consultationEndTime;
        double consultationAmount;

        try {
            consultationDate = LocalDate.parse(consultationDTO.getConsultationDTODate());
        } catch (Exception e) {
            throw new ValidationException("La fecha tiene un formato inválido. Debe ser yyyy-MM-dd");
        }

        try {
            consultationStartTime = LocalTime.parse(consultationDTO.getConsultationDTOStartTime());
        } catch (Exception e) {
            throw new ValidationException("El horario de inicio tiene un formato inválido. Debe ser HH:mm");
        }

        try {
            consultationEndTime = LocalTime.parse(consultationDTO.getConsultationDTOEndTime());
        } catch (Exception e) {
            throw new ValidationException("El horario de fin tiene un formato inválido. Debe ser HH:mm");
        }

        if (!consultationStartTime.isBefore(consultationEndTime)) {
            throw new ValidationException("El horario de inicio debe ser anterior al horario de fin");
        }

        try {
            ConsultationStatus.valueOf(consultationDTO.getConsultationDTOStatus());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ValidationException("El estado de la consulta no es válido");
        }

        try {
            consultationAmount = Double.parseDouble(consultationDTO.getConsultationDTOAmount());
        } catch (NumberFormatException e) {
            throw new ValidationException("El monto debe ser un número válido");
        }

        if (consultationAmount <= 0) {
            throw new ValidationException("El monto de la consulta debe ser mayor a cero");
        }

        // Validar duplicidad de fecha y hora de inicio (ignorando la propia consulta si es update)
        UUID currentId = Optional.ofNullable(consultationDTO.getConsultationDTOId())
                                 .filter(s -> !s.isBlank())
                                 .map(UUID::fromString)
                                 .orElse(null);

        if (currentId != null) {
            // Validar para update, excluyendo la misma consulta
            if (consultationDAO.isConsultationStartDatetimeExists(consultationDate, consultationStartTime, currentId)) {
                throw new ValidationException("Ya existe una consulta en la fecha y hora indicada: " + consultationDate);
            }
        } else {
            // Validar para insert
            if (consultationDAO.isConsultationStartDatetimeExists(consultationDate, consultationStartTime)) {
                throw new ValidationException("Ya existe una consulta en la fecha y hora indicada: " + consultationDate);
            }
        }
    }
    
    /**
     * Crea un objeto Consultation a partir de un ConsultationDTO
     */
    private Consultation createConsultationFromConsultationDTO(ConsultationDTO consultationDTO) {
        UUID consultationId = Optional.ofNullable(consultationDTO.getConsultationDTOId())
                                .filter(s -> !s.isBlank())
                                .map(UUID::fromString)
                                .orElseGet(UUID::randomUUID);
        
        return new Consultation(
            consultationId,
            LocalDate.parse(consultationDTO.getConsultationDTODate()),
            LocalTime.parse(consultationDTO.getConsultationDTOStartTime()),
            LocalTime.parse(consultationDTO.getConsultationDTOEndTime()),
            Double.valueOf(consultationDTO.getConsultationDTOAmount()),
            ConsultationStatus.valueOf(consultationDTO.getConsultationDTOStatus())
        );
    }
    
    /**
     * Crea un objeto ConsultationDTO a partir de un Consultation
     */
    private ConsultationDTO createConsultationDTOFromConsultation(Consultation c) {
        ConsultationDTO dto = new ConsultationDTO();
        dto.setConsultationDTOId(c.getConsultationId().toString());
        dto.setConsultationDTODate(c.getConsultationDate().toString());
        dto.setConsultationDTOStartTime(c.getConsultationStartTime().toString());
        dto.setConsultationDTOEndTime(c.getConsultationEndTime().toString());
        dto.setConsultationDTOAmount(c.getConsultationAmount().toString());
        dto.setConsultationDTOStatus(c.getConsultationStatus().toString());
        
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
}