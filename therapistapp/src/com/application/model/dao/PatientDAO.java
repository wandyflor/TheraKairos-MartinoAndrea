package com.application.model.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.application.exceptions.runtimeExceptions.dataAccessException.ConstraintViolationException;
import com.application.exceptions.runtimeExceptions.dataAccessException.DataAccessException;
import com.application.exceptions.runtimeExceptions.dataAccessException.EntityNotFoundException;
import com.application.model.entities.Patient;

public class PatientDAO {
    private static final String URL = "jdbc:mysql://localhost:3306/therapist_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    
    private static final String SELECT_ALL =
        "SELECT * FROM tbl_patient " +
        "WHERE is_active = true ORDER BY patient_last_name";

    private static final String SELECT_BY_ID =
        "SELECT * FROM tbl_patient " +
        "WHERE patient_id = ? AND is_active = true";

    private static final String SELECT_BY_DNI =
        "SELECT * FROM tbl_patient " +
        "WHERE patient_dni = ? AND is_active = true";

    private static final String INSERT_SQL =
        "INSERT INTO tbl_patient ( " +
        "patient_id, " +
        "patient_dni, " +
        "patient_name, " +
        "patient_last_name, " +
        "patient_birth_date, " +
        "patient_occupation, " +
        "patient_phone, " +
        "patient_email, " +
        "city_id, " +
        "patient_address, " +
        "patient_address_number, " +
        "patient_address_floor, " +
        "patient_address_department" +
        ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_SQL =
        "UPDATE tbl_patient SET " +
        "patient_dni = ?, " +
        "patient_name = ?, " +
        "patient_last_name = ?, " +
        "patient_birth_date = ?, " +
        "patient_occupation = ?, " +
        "patient_phone = ?, " +
        "patient_email = ?, " +
        "city_id = ?, " +
        "patient_address = ?, " +
        "patient_address_number = ?, " +
        "patient_address_floor = ?, " +
        "patient_address_department = ? " +
        "WHERE patient_id = ?";

    private static final String DELETE_SQL =
        "UPDATE tbl_patient SET " +
        "is_active = false " +
        "WHERE patient_id = ?";

    private static final String EXISTS_DNI_SQL =
        "SELECT 1 FROM tbl_patient " +
        "WHERE patient_dni = ? AND is_active = true LIMIT 1";

    private static final String EXISTS_EMAIL_SQL =
        "SELECT 1 FROM tbl_patient " +
        "WHERE patient_email = ? AND is_active = true LIMIT 1";
    
    private static final int MYSQL_DUPLICATE_ERROR   = 1062;
    private static final String UNIQUE_DNI_CONSTRAINT   = "uk_patient_dni";
    private static final String UNIQUE_EMAIL_CONSTRAINT = "uk_patient_email";

    /**
     * Obtiene todos los pacientes de la base de datos
     * @return Lista de pacientes
     * @throws DataAccessException Si ocurre un error al acceder a la base de datos
     */
    public List<Patient> getAllPatients() {
        List<Patient> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToPatient(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Error al listar pacientes", e);
        }
    }
    
    /**
     * Inserta un nuevo paciente en la base de datos
     * @param patient Paciente a insertar
     * @throws ConstraintViolationException Si se viola una restricción única
     * @throws DataAccessException Si ocurre otro error al acceder a la base de datos
     */
    public void insertPatient(Patient patient) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {

            ps.setString(1, patient.getPatientId().toString());
            ps.setString(2, patient.getPatientDNI());
            ps.setString(3, patient.getPatientName());
            ps.setString(4, patient.getPatientLastName());
            ps.setDate(5, Date.valueOf(patient.getPatientBirthDate()));
            ps.setString(6, patient.getPatientOccupation());
            ps.setString(7, patient.getPatientPhone());
            ps.setString(8, patient.getPatientEmail());
            ps.setString(9, patient.getCityId().toString());
            ps.setString(10, patient.getPatientAddress());
            ps.setInt(11, patient.getPatientAddressNumber());

            if (patient.getPatientAddressFloor() >= 0) {
                ps.setInt(12, patient.getPatientAddressFloor());
            } else {
                ps.setNull(12, Types.INTEGER);
            }

            if (patient.getPatientAddressDepartment() != null) {
                ps.setString(13, patient.getPatientAddressDepartment());
            } else {
                ps.setNull(13, Types.VARCHAR);
            }
            
            ps.executeUpdate();

        } catch (SQLException e) {
            handleConstraintViolation(e, "Patient");
            throw new DataAccessException("Error al insertar paciente", e);
        }
    }
    
    /**
     * Modifica un paciente existente en la base de datos
     * @param patient Paciente a modificar
     * @throws ConstraintViolationException Si se viola una restricción única
     * @throws DataAccessException Si ocurre otro error al acceder a la base de datos
     */
    public void updatePatient(Patient patient) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {

            ps.setString(1, patient.getPatientDNI());
            ps.setString(2, patient.getPatientName());
            ps.setString(3, patient.getPatientLastName());
            ps.setDate(4, Date.valueOf(patient.getPatientBirthDate()));
            ps.setString(5, patient.getPatientOccupation());
            ps.setString(6, patient.getPatientPhone());
            ps.setString(7, patient.getPatientEmail());
            ps.setString(8, patient.getCityId().toString());
            ps.setString(9, patient.getPatientAddress());
            ps.setInt(10, patient.getPatientAddressNumber());

            if (patient.getPatientAddressFloor() >= 0) {
                ps.setInt(11, patient.getPatientAddressFloor());
            } else {
                ps.setNull(11, Types.INTEGER);
            }

            if (patient.getPatientAddressDepartment() != null) {
                ps.setString(12, patient.getPatientAddressDepartment());
            } else {
                ps.setNull(12, Types.VARCHAR);
            }
            ps.setString(13, patient.getPatientId().toString());

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new EntityNotFoundException("Patient", patient.getPatientId().toString());
            }

        } catch (SQLException e) {
            handleConstraintViolation(e, "Patient");
            throw new DataAccessException("Error al actualizar paciente", e);
        }
    }
    
    /**
     * Eliminar un paciente existente en la base de datos
     * @param patientId del paciente a eliminar
     * @throws EntityNotFoundException Si no se encuentra el paciente
     * @throws DataAccessException Si ocurre otro error al acceder a la base de datos
     */
    public void deletePatient(UUID patientId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_SQL)) {

            ps.setString(1, patientId.toString());
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new EntityNotFoundException("Patient", patientId.toString());
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error al eliminar paciente", e);
        }
    }
    
    /**
     * Busca un paciente por su Id
     * @param patientId del paciente a buscar
     * @return El paciente encontrado
     * @throws EntityNotFoundException Si no se encuentra el paciente
     * @throws DataAccessException Si ocurre un error al acceder a la base de datos
     */
    public Patient getPatientById(UUID patientId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {

            ps.setString(1, patientId.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPatient(rs);
                } else {
                    throw new EntityNotFoundException("Patient", patientId.toString());
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error al buscar paciente por ID", e);
        }
    }
        
    /**
     * Busca un paciente por su DNI
     * @param patientDNI del paciente a buscar
     * @return El paciente encontrado
     * @throws EntityNotFoundException Si no se encuentra el paciente
     * @throws DataAccessException Si ocurre un error al acceder a la base de datos
     */
    public Patient getPatientByDNI(String patientDNI) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_DNI)) {

            ps.setString(1, patientDNI);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPatient(rs);
                } else {
                    throw new EntityNotFoundException("Patient", patientDNI);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error al buscar paciente por DNI", e);
        }
    }
    
    /**
     * Verifica si existe un paciente con el DNI especificado
     * @param patientDNI DNI del paciente a verificar
     * @return true si existe, false si no
     * @throws DataAccessException Si ocurre un error al acceder a la base de datos
     */
    public boolean isPatientDNIExists(String patientDNI) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(EXISTS_DNI_SQL)) {

            ps.setString(1, patientDNI);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error al verificar DNI existente", e);
        }
    }
    
    /**
     * Verifica si existe un paciente con el email especificado
     * @param patientEmail Email del paciente a verificar
     * @return true si existe, false si no
     * @throws DataAccessException Si ocurre un error al acceder a la base de datos
     */
    public boolean isPatientEmailExists(String patientEmail) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(EXISTS_EMAIL_SQL)) {

            ps.setString(1, patientEmail);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error al verificar email existente", e);
        }
    }
        
    private void handleConstraintViolation(SQLException e, String entity) {
        if (e.getErrorCode() == MYSQL_DUPLICATE_ERROR) {
            String msg = e.getMessage().toLowerCase();
            if (msg.contains(UNIQUE_DNI_CONSTRAINT)) {
                throw new ConstraintViolationException(entity, "DNI");
            } else if (msg.contains(UNIQUE_EMAIL_CONSTRAINT)) {
                throw new ConstraintViolationException(entity, "email");
            }
        }
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