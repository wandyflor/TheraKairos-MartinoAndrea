package com.application.model.dao;

import com.application.exceptions.runtimeExceptions.dataAccessException.ConstraintViolationException;
import com.application.exceptions.runtimeExceptions.dataAccessException.DataAccessException;
import com.application.exceptions.runtimeExceptions.dataAccessException.EntityNotFoundException;
import com.application.model.entities.ConsultationPatient;
import com.application.model.entities.Patient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ConsultationPatientDAO {
    private static final String URL = "jdbc:mysql://localhost:3306/therapist_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    
    private static final String INSERT_SQL =
        "INSERT INTO tbl_consultation_patient ( " +
        "consultation_id, " +
        "patient_id, " +
        "is_paid " +
        ") VALUES (?, ?, ?)";
    
    private static final String DELETE_SQL =
        "UPDATE tbl_consultation_patient SET " +
        "is_active = false " +
        "WHERE consultation_id = ? and patient_id = ?";
    
    private static final String UPDATE_PATIENT_IS_ACTIVE = 
        "UPDATE tbl_consultation_patient SET " +
        "is_active = true, is_paid = ? " +
        "WHERE consultation_id = ? AND patient_id = ?";
    
    private static final String SELECT_PATIENT_IS_ACTIVE = 
        "SELECT COUNT(*) FROM tbl_consultation_patient " +
        "WHERE consultation_id = ? AND patient_id = ? AND is_active = false";
    
    private static final String DELETE_ALL_PATIENTS_BY_CONSULTATION_ID =
        "UPDATE tbl_consultation_patient SET " +
        "is_active = false " +
        "WHERE consultation_id = ?";
    
    private static final String DELETE_ALL_CONSULTATION_BY_PATIENT_ID = 
        "UPDATE tbl_consultation_patient SET " +
        "is_active = false " +
        "WHERE patient_id = ?";    
    
    private static final String UPDATE_PATIENT_IS_PAID =
        "UPDATE tbl_consultation_patient SET " +
        "is_paid = true " +
        "WHERE consultation_id = ? and patient_id = ?";

    private static final String SELECT_PATIENTS_BY_CONSULTATION_ID =
        "SELECT * FROM tbl_patient p " +
        "JOIN tbl_consultation_patient cp ON p.patient_id = cp.patient_id " +
        "WHERE cp.consultation_id = ? AND cp.is_active = true";
    
    private static final String SELECT_PATIENTS_ID_BY_CONSULTATION_ID =
        "SELECT cp.patient_id FROM tbl_consultation_patient cp " +
        "WHERE cp.consultation_id = ? AND cp.is_active = true";
        
    private static final String UNIQUE_CONSULTATION_PATIENT_CONSTRAINT = "uk_consultation_time";

    /**
     * Inserta un nuevo paciente a una consulta existente en la base de datos
     * @param consultationPatient Paciente a insertar
     * @throws ConstraintViolationException Si se viola una restricción única
     * @throws DataAccessException Si ocurre otro error al acceder a la base de datos
     */
    public void insertConsultationPatient(ConsultationPatient consultationPatient) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {

            ps.setString(1, consultationPatient.getConsultationId().toString());
            ps.setString(2, consultationPatient.getPatientId().toString());
            ps.setBoolean(3, consultationPatient.getIsPaid());

            ps.executeUpdate();

        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains(UNIQUE_CONSULTATION_PATIENT_CONSTRAINT)) {
                throw new ConstraintViolationException("Consultation", "start datetime");
            }
            throw new DataAccessException("Error al insertar consulta", e);
        }
    }
    
    /**
     * Elimina (logicamente) un paciente de una consulta existente en la base de datos
     * @param consultationId de la consulta a eliminar
     * @param patientId del paciente
     * @throws EntityNotFoundException Si no se encuentra la consulta
     * @throws DataAccessException Si ocurre otro error al acceder a la base de datos
     */
    public void deleteConsultationPatient(UUID consultationId, UUID patientId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_SQL)) {

            ps.setString(1, consultationId.toString());
            ps.setString(2, patientId.toString());

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new EntityNotFoundException("consultation", consultationId.toString());
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error al eliminar consulta", e);
        }
    }
    
    /**
     * Modifica el estado (is_active = true) del paciente en la base de datos
     * @param consultationId Identificador de la consulta
     * @param patientId Identificador del paciente
     * @param isPaid Booleano del estado del pago
     * @throws DataAccessException Si ocurre otro error al acceder a la base de datos
     */
    public void reactivateConsultationPatient(UUID consultationId, UUID patientId, boolean isPaid) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_PATIENT_IS_ACTIVE)) {

            ps.setBoolean(1, isPaid);
            ps.setString(2, consultationId.toString());
            ps.setString(3, patientId.toString());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Error al reactivar paciente en consulta", e);
        }
    }
    
    /**
     * Verifica el estado del paciente en la base de datos
     * @param consultationId Identificador de la consulta
     * @param patientId Identificador del paciente
     * @return Boolean del estado del paciente
     * @throws DataAccessException Si ocurre otro error al acceder a la base de datos
     */
    public boolean existsInactiveConsultationPatient(UUID consultationId, UUID patientId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_PATIENT_IS_ACTIVE)) {

            ps.setString(1, consultationId.toString());
            ps.setString(2, patientId.toString());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

            return false;

        } catch (SQLException e) {
            throw new DataAccessException("Error al verificar paciente inactivo en consulta", e);
        }
    }
    
    /**
     * Elimina (logicamente) todos los pacientes de una consulta existente en la base de datos
     * @param consultationId de la consulta a eliminar
     * @throws EntityNotFoundException Si no se encuentra la consulta
     * @throws DataAccessException Si ocurre otro error al acceder a la base de datos
     */
    public void deleteAllConsultationPatients(UUID consultationId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_ALL_PATIENTS_BY_CONSULTATION_ID)) {

            ps.setString(1, consultationId.toString());

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new EntityNotFoundException("consultation", consultationId.toString());
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error al eliminar consulta", e);
        }
    }
    
    /**
     * Elimina (logicamente) todas las consultas asociadas a un paciente determinado en la base de datos
     * @param patientId del paciente
     * @throws EntityNotFoundException Si no se encuentra la consulta
     * @throws DataAccessException Si ocurre otro error al acceder a la base de datos
     */
    public void deletePatientFromAllConsultation(UUID patientId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_ALL_CONSULTATION_BY_PATIENT_ID)) {

            ps.setString(1, patientId.toString());

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new EntityNotFoundException("patient", patientId.toString());
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error al eliminar consultas asociadas al paciente", e);
        }
    }
    
    /**
     * Se modifica el estado del pago de la consulta existente en la base de datos
     * @param consultationId de la consulta
     * @param patientId del paciente
     * @throws EntityNotFoundException Si no se encuentra la consulta
     * @throws DataAccessException Si ocurre otro error al acceder a la base de datos
     */
    public void setConsultationPatientIsPaid(UUID consultationId, UUID patientId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_PATIENT_IS_PAID)) {

            ps.setString(1, consultationId.toString());
            ps.setString(2, patientId.toString());

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new EntityNotFoundException("consultation", consultationId.toString());
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error al eliminar consulta", e);
        }
    }
    
    /**
     * Obtiene todos los pacientes de una consulta determinada
     * @param consultationId Id de la consulta a buscar
     * @return Lista objetos de ConsultationPatient para la consulta especificada
     * @throws DataAccessException Si ocurre un error al acceder a la base de datos
     */
    public List<ConsultationPatient> getPatientsByConsultationId(UUID consultationId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_PATIENTS_BY_CONSULTATION_ID)) {

            ps.setString(1, consultationId.toString());
            try (ResultSet rs = ps.executeQuery()) {
                List<ConsultationPatient> consultationPatients = new ArrayList<>();
                while (rs.next()) {
                    consultationPatients.add(mapResultSetToConsultationPatient(rs));
                }
                return consultationPatients;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error obteniendo pacientes por consulta", e);
        }
    }
    
    /**
     * Obtiene todos los Identificadores de los pacientes de una consulta determinada
     * @param consultationId Id de la consulta a buscar
     * @return Lista de los Identificadores de los pacientes para la consulta especificada 
     * @throws DataAccessException Si ocurre un error al acceder a la base de datos
     */
    public List<UUID> getPatientsIdByConsultationId(UUID consultationId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_PATIENTS_ID_BY_CONSULTATION_ID)) {

            ps.setString(1, consultationId.toString());
            try (ResultSet rs = ps.executeQuery()) {
                List<UUID> patientsUUID = new ArrayList<>();
                while (rs.next()) {
                    patientsUUID.add(UUID.fromString(rs.getString("patient_id")));
                }
                return patientsUUID;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error obteniendo pacientes por consulta", e);
        }
    }
    
    /**
     * Mapea un ResultSet a un objeto ConsultationPatient
     */
    private ConsultationPatient mapResultSetToConsultationPatient(ResultSet rs) throws SQLException {
        return new ConsultationPatient(
            UUID.fromString(rs.getString("consultation_id")),
            UUID.fromString(rs.getString("patient_id")),
            rs.getBoolean("is_paid")
        );
    }

    /**
     * Mapea un ResultSet en un Patient
     * @param ResultSet del paciente
     */
    private Patient mapResultSetToPatient(ResultSet rs) throws SQLException {
        return new Patient(
            UUID.fromString(rs.getString("patient_id")),
            rs.getString("patient_dni"),
            rs.getString("patient_name"),
            rs.getString("patient_last_name"),
            rs.getDate("patient_birth_date").toLocalDate(),
            rs.getString("patient_occupation"),
            rs.getString("patient_phone"),
            rs.getString("patient_email"),
            UUID.fromString(rs.getString("city_id")),
            rs.getString("patient_address"),
            rs.getInt("patient_address_number"),
            rs.getInt("patient_address_floor"),
            rs.getString("patient_address_department")
        );
    }
    
    /**
     * Obtiene una conexión a la base de datos
     */
    private Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new DataAccessException("Error al conectar con la base de datos", e);
        }
    }
}