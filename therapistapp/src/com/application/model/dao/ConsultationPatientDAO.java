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
        "is_paid, " +
        "patient_note_path " +
        ") VALUES (?, ?, ?, ?, ?)";
    
    private static final String UPDATE_SQL =
        "UPDATE tbl_consultation_patient SET " +
        "is_paid = ?, " +
        "WHERE consultation_id = ? and patient_id = ?";
    
    private static final String DELETE_SQL =
        "UPDATE tbl_consultation_patient SET is_active = false WHERE consultation_id = ? and patient_id = ?";

    private static final String SELECT_PATIENTS_BY_CONSULTATION =
            "SELECT * FROM tbl_patient p " +
            "JOIN tbl_consultation_patient cp ON p.patient_id = cp.patient_id " +
            "WHERE cp.consultation_id = ?";
        
    private static final String UPDATE_IS_PAID_TRUE =
        "UPDATE tbl_consultation_patient SET " +
        "is_paid = true " +
        "WHERE consultation_id = ? and patient_id = ?";
    
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
            ps.setString(4, consultationPatient.getPatientNotePath());

            ps.executeUpdate();

        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains(UNIQUE_CONSULTATION_PATIENT_CONSTRAINT)) {
                throw new ConstraintViolationException("Consultation", "start datetime");
            }
            throw new DataAccessException("Error al insertar consulta", e);
        }
    }
    
    /**
     * Eliminar un paciente de una consulta existente en la base de datos (borrado logico)
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
     * Obtiene todos los pacientes de una consulta determinada
     * @param consultationId Id de la consulta a buscar
     * @return Lista de pacientes para la consulta especificada especificada
     * @throws DataAccessException Si ocurre un error al acceder a la base de datos
     */
    public List<Patient> getPatientsByConsultationId(UUID consultationId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_PATIENTS_BY_CONSULTATION)) {

            ps.setString(1, consultationId.toString());
            try (ResultSet rs = ps.executeQuery()) {
                List<Patient> patients = new ArrayList<>();
                while (rs.next()) {
                    patients.add(mapResultSetToPatient(rs));
                }
                return patients;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error obteniendo pacientes por consulta", e);
        }
    }

    /**
     * Modifica un tuple de la base de datos indicando que la consulta esta paga (is_paid = true)
     * @param consultationId de la consulta a modificar
     * @param patientId del paciente a modificar
     * @throws DataAccessException Si ocurre un error al acceder a la base de datos
     */
    public void setConsultationPatientPaid(UUID consultationId, UUID patientId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_IS_PAID_TRUE)) {

            ps.setString(1, consultationId.toString());
            ps.setString(2, patientId.toString());

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new EntityNotFoundException("consultation", consultationId.toString());
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error al actualizar estado de pago", e);
        }
    }
    
    /**
     * Mapea un ResultSet a un objeto ConsultationPatient
     */
    private ConsultationPatient mapResultSetToConsultationPatient(ResultSet rs) throws SQLException {
        return new ConsultationPatient(
            UUID.fromString(rs.getString("consultation_id")),
            UUID.fromString(rs.getString("patient_id")),
            rs.getBoolean("is_paid"),
            rs.getString("patient_note_path")
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