package com.application.services;

import com.application.exceptions.businessException.BusinessException;
import com.application.exceptions.businessException.ValidationException;
import com.application.exceptions.runtimeExceptions.dataAccessException.ConstraintViolationException;
import com.application.exceptions.runtimeExceptions.dataAccessException.DataAccessException;
import com.application.exceptions.runtimeExceptions.dataAccessException.EntityNotFoundException;
import com.application.model.dao.CityDAO;
import com.application.model.dto.CityDTO;
import com.application.model.entities.City;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CityService {
    private final CityDAO cityDAO;

    public CityService() {
        this.cityDAO = new CityDAO();
    }

    /**
     * Obtiene todas las ciudades convertidas a DTO
     * @return Lista de CityDTO
     * @throws BusinessException Si ocurre un error al acceder a los datos
     */
    public List<CityDTO> getAllCities() throws BusinessException {
        try {
            return cityDAO.getAllCities().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            throw new BusinessException("Error al obtener el listado de ciudades", e);
        }
    }

    /**
     * Inserta una nueva ciudad
     * @param cityDTO Datos de la ciudad a insertar
     * @throws ValidationException Si los datos no son válidos o la ciudad ya existe
     * @throws BusinessException Si ocurre otro error de negocio
     */
    public void insertCity(CityDTO cityDTO) throws ValidationException, BusinessException {
        try {
            validateCityData(cityDTO);
            City city = createCityFromDTO(cityDTO);
            cityDAO.insertCity(city);
        } catch (ConstraintViolationException e) {
            throw new ValidationException("La ciudad ya existe en el sistema");
        } catch (DataAccessException e) {
            throw new BusinessException("Error al guardar la ciudad en el sistema", e);
        }
    }
    
    /**
    * Modifica una ciudad existente
    * @param cityDTO Datos de la ciudad a modificar
    * @throws ValidationException Si los datos no son válidos o la ciudad no existe
    * @throws BusinessException Si ocurre otro error de negocio
    */
    public void updateCity(CityDTO cityDTO) throws ValidationException, BusinessException {
        try {
            validateCityData(cityDTO);
            City city = createCityFromDTO(cityDTO);
            cityDAO.updateCity(city);
        } catch (EntityNotFoundException e) {
            throw new ValidationException("No existe la ciudad con Id '" + cityDTO.getCityId() + "'");
        } catch (ConstraintViolationException e) {
            throw new ValidationException("Ya existe otra ciudad con nombre '" + cityDTO.getCityName().trim() + "'");
        } catch (DataAccessException e) {
            throw new BusinessException("Error al actualizar la ciudad en el sistema", e);
        }
    }
    
    /**
    * Elimina una ciudad existente
    * @param cityId campo clave de entidad City a eliminar
    * @throws ValidationException Si la ciudad no existe para el parametro ingresado
    * @throws BusinessException Si ocurre un error durante el proceso
    */    
    public void deleteCity(String cityId) throws ValidationException, BusinessException {
        try {
            cityDAO.deleteCity(UUID.fromString(cityId));
        } catch (EntityNotFoundException e) {
            throw new ValidationException("No existe la ciudad con Id '" + cityId + "'");
        } catch (DataAccessException e) {
            throw new BusinessException("Error al eliminar la ciudad del sistema", e);
        }
    }
    
    /**
     * Obtiene el nombre de la ciudad asociado al Id
     * @param cityId
     * @return String
     * @throws @throws ValidationException Si los datos no son válidos o la ciudad no existe
     * @throws BusinessException Si ocurre un error al acceder a los datos
     */
    public String getCityNameById(String cityId) throws ValidationException, BusinessException {
        try {
            return cityDAO.getCityNameById(UUID.fromString(cityId));
        } catch (EntityNotFoundException e) {
            throw new ValidationException("No existe la ciudad con Id '" + cityId + "'");
        } catch (DataAccessException e) {
            throw new BusinessException("Error al obtener la ciudad", e);
        }
    }
    
    /**
    * Validacion los datos de la ciudad antes de la inserción
    * @param cityDTO Datos de la ciudad a validar sus campos
    * @throws ValidationException Si algun dato no es válido
    */
    private void validateCityData(CityDTO cityDTO) throws ValidationException, DataAccessException {
        String name = cityDTO.getCityName();
        String zipCode = cityDTO.getCityZIPCode();

        if (isNullOrEmpty(cityDTO.getCityName())) {
            throw new ValidationException("El nombre de la ciudad es requerido");
        }
        if (name.length() > 100) {
            throw new ValidationException("El nombre de la ciudad no puede exceder 100 caracteres");
        }

        if (isNullOrEmpty(cityDTO.getCityZIPCode())) {
            throw new ValidationException("El código postal es requerido");
        }
        if (!zipCode.matches("\\d{4,10}")) {
            throw new ValidationException("El código postal debe ser numérico (4 a 10 dígitos)");
        }

        // Verificación de unicidad sólo en inserción, o si cambió el nombre en actualización
        if (!cityDAO.isCityNameExists(cityDTO.getCityName())) {
            if (cityDAO.isCityNameExists(name.trim())) {
                throw new ValidationException("Ya existe una ciudad con nombre: " + name.trim());
            }
        }
    }

    /**
    * Crea un objeto City a partir de un CityDTO
    * @param cityDTO Datos de la ciudad a convertir en City
    */
    private City createCityFromDTO(CityDTO cityDTO) {
        return new City(
            UUID.randomUUID(),
            cityDTO.getCityName().trim().toLowerCase(),
            cityDTO.getCityZIPCode().trim().toLowerCase()
        );
    }
    
    /**
    * Convierte una entidad City a CityDTO
    * @param city Datos de la ciudad a convertir en CityDTO
    */
    private CityDTO convertToDTO(City city) {
        return new CityDTO(
            city.getCityId().toString(),
            city.getCityName(),
            city.getCityZIPCode()
        );
    }
    
    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }   
}