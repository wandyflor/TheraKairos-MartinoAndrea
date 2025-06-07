package com.application.model.dao;

import com.application.exceptions.runtimeExceptions.dataAccessException.ConstraintViolationException;
import com.application.exceptions.runtimeExceptions.dataAccessException.DataAccessException;
import com.application.exceptions.runtimeExceptions.dataAccessException.EntityNotFoundException;
import com.application.model.entities.Consultation;
import com.application.model.enumerations.ConsultationStatus;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ConsultationDAO {
    private static final String URL = "jdbc:mysql://localhost:3306/therapist_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    
    private static final String INSERT_SQL =
        "INSERT INTO tbl_consultation ( " +
        "consultation_id, " +
        "consultation_date, " +
        "consultation_start_time, " +
        "consultation_end_time, " +
        "consultation_amount, " +
        "consultation_status " +
        ") VALUES (?, ?, ?, ?)";
    
    private static final String UPDATE_SQL =
        "UPDATE tbl_consultation SET " +
        "consultation_date = ?, " +
        "consultation_start_time = ?, " +
        "consultation_end_time = ?, " +
        "consultation_amount = ?," +
        "consultation_status = ? " +
        "WHERE consultation_id = ?";
    
    private static final String DELETE_SQL =
        "UPDATE tbl_consultation SET is_active = false WHERE consultation_id = ?";

    private static final String SELECT_CONSULTATION_BY_ID =
        "SELECT * FROM tbl_consultation WHERE consultation_id = ? and is_active = true";
        
    private static final String SELECT_CONSULTATION_BY_DATE =
        "SELECT * FROM tbl_consultation WHERE consultation_date = ? and is_active = true ORDER BY consultation_start_time";
    
    private static final String CHECK_START_DATETIME_SQL =
        "SELECT COUNT(*) FROM tbl_consultation " +
        "WHERE consultation_date = ? AND consultation_start_time = ?";
    
    private static final String UNIQUE_CONSULTATION_TIME_CONSTRAINT = "uk_consultation_time";

    /**
     * Inserta una nueva consulta en la base de datos
     * @param consultation Consulta a insertar
     * @throws ConstraintViolationException Si se viola una restricción única
     * @throws DataAccessException Si ocurre otro error al acceder a la base de datos
     */
    public void insertConsultation(Consultation consultation) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {

            ps.setString(1, consultation.getConsultationId().toString());
            ps.setObject(2, consultation.getConsultationDate());
            ps.setObject(3, consultation.getConsultationStartTime());
            ps.setObject(4, consultation.getConsultationEndTime());
            ps.setDouble(5, consultation.getConsultationAmount());
            ps.setString(6, consultation.getConsultationStatus().toString());

            ps.executeUpdate();

        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains(UNIQUE_CONSULTATION_TIME_CONSTRAINT)) {
                throw new ConstraintViolationException("Consultation", "start datetime");
            }
            throw new DataAccessException("Error al insertar consulta", e);
        }
    }
    
    /**
     * Modifica una consulta existente en la base de datos
     * @param consultation Consulta a modificar
     * @throws EntityNotFoundException Si no se encuentra la consulta
     * @throws ConstraintViolationException Si se viola la clave única de tiempo
     * @throws DataAccessException Si ocurre otro error al acceder a la base de datos
     */
    public void updateConsultation(Consultation consultation) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {

            ps.setObject(1, consultation.getConsultationDate());
            ps.setObject(2, consultation.getConsultationStartTime());
            ps.setObject(3, consultation.getConsultationEndTime());
            ps.setDouble(4, consultation.getConsultationAmount());
            ps.setString(5, consultation.getConsultationStatus().toString());
            
            ps.setString(6, consultation.getConsultationId().toString());

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new EntityNotFoundException("consultation", consultation.getConsultationId().toString());
            }

        } catch (SQLException e) {
             if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains(UNIQUE_CONSULTATION_TIME_CONSTRAINT)) {
                throw new ConstraintViolationException("Consultation", "start datetime");
            }
            throw new DataAccessException("Error al actualizar consulta", e);
        }
    }
    
    /**
     * Eliminar una consulta existente en la base de datos (borrado logico)
     * @param consultationId de la consulta a eliminar
     * @throws EntityNotFoundException Si no se encuentra la consulta
     * @throws DataAccessException Si ocurre otro error al acceder a la base de datos
     */
    public void deleteConsultation(UUID consultationId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_SQL)) {

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
     * Obtiene la consulta para un identificador determinado 
     * @param consultationId Identificador de la consulta
     * @return consulta asociada al identificador
     * @throws DataAccessException Si ocurre un error al acceder a la base de datos
     */
    public Consultation getConsultationById(UUID consultationId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_CONSULTATION_BY_ID)) {
            
            ps.setString(1, consultationId.toString());
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToConsultation(rs);
                } else {
                    throw new EntityNotFoundException("Consultation", consultationId.toString());
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error al obtener la consulta", e);
        }
    }
    
    /**
     * Obtiene todas las consultas para una fecha específica
     * @param consultationDate Fecha de las consultas a buscar
     * @return Lista de consultas para la fecha especificada
     * @throws DataAccessException Si ocurre un error al acceder a la base de datos
     */
    public List<Consultation> getConsultationsByDate(Date consultationDate) {
        List<Consultation> consultations = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_CONSULTATION_BY_DATE)) {
            
            ps.setDate(1, consultationDate);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Consultation consultation = mapResultSetToConsultation(rs);
                    consultations.add(consultation);
                }
            }
            
            return consultations;

        } catch (SQLException e) {
            throw new DataAccessException("Error al obtener consultas por fecha", e);
        }
    }
        
    /**
     * Verifica si existe una consulta con la fecha/hora de inicio indicada
     * @param consultationDate Fecha de la consulta
     * @param consultationStartTime horario de inicio de la consulta
     * @return true si ya existe, false en caso contrario
     * @throws DataAccessException Si ocurre un error al acceder a la base de datos
     */
    public boolean isConsultationStartDatetimeExists(LocalDate consultationDate, LocalTime consultationStartTime) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(CHECK_START_DATETIME_SQL)) {

            ps.setDate(1, Date.valueOf(consultationDate));          // Correcto para LocalDate → java.sql.Date
            ps.setTime(2, Time.valueOf(consultationStartTime));     // Correcto para LocalTime → java.sql.Time

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            String message = String.format("Error al verificar existencia de consulta en %s %s", consultationDate, consultationStartTime);
            throw new DataAccessException(message, e);
        }
    }

    
    /**
     * Mapea un ResultSet a un objeto Consultation
     */
    private Consultation mapResultSetToConsultation(ResultSet rs) throws SQLException {
        return new Consultation(
            UUID.fromString(rs.getString("consultation_id")),
            rs.getDate("consultation_date").toLocalDate(),
            rs.getTime("consultation_start_time").toLocalTime(),    
            rs.getTime("consultation_end_time").toLocalTime(),
            rs.getDouble("consultation_amount"),
            ConsultationStatus.valueOf(rs.getString("consultation_status"))
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