package com.application.controllers.entities;

import com.application.exceptions.businessException.BusinessException;
import com.application.exceptions.businessException.ValidationException;
import com.application.model.dto.CityDTO;
import com.application.services.CityService;

import java.util.List;

public class CityController {
    private final CityService cityService;
    
    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    /**
     * Obtiene todas las ciudades convertidas a DTO
     * @return Lista de CityDTO
     * @throws BusinessException Si ocurre un error al acceder a los datos
     */
    public List<CityDTO> getAllCities() throws BusinessException {
        return cityService.getAllCities();
    }

    /**
     * Inserta una nueva ciudad
     * @param cityDTO Datos de la ciudad a insertar
     * @throws ValidationException Si los datos no son válidos o la ciudad ya existe
     * @throws BusinessException Si ocurre otro error de negocio
     */
    public void insertCity(CityDTO cityDTO) throws ValidationException, BusinessException {
        validateCityData(cityDTO);
        cityService.insertCity(cityDTO);
    }

    /**
     * Modifica una ciudad existente.
     * @param cityDTO de la ciudad a modificar
     * @throws ValidationException si falta Id o algún dato es inválido
     * @throws BusinessException si ocurre un error en la capa de servicio
     */
    public void updateCity(CityDTO cityDTO) throws ValidationException, BusinessException {
        validateCityData(cityDTO);
        cityService.updateCity(cityDTO);
    }

    /**
     * Elimina una ciudad por su ID.
     * @param cityId de la ciudad a borrar (en formato string)
     * @throws ValidationException si el Id es nulo o no existe
     * @throws BusinessException si ocurre un error en la capa de servicios
     */
    public void deleteCity(String cityId) throws ValidationException, BusinessException {
        if (cityId == null) {
            throw new ValidationException("El Id de la ciudad es requerido");
        }
        
        cityService.deleteCity(cityId);
    }

    /**
     * Obtiene el nombre de la ciudad (cityName) por su Id (cityId).
     * @param cityId de la ciudad a buscar el nombre (en formato string)
     * @return Nombre de la ciudad (cityName)
     * @throws ValidationException si el Id es nulo o no existe
     * @throws BusinessException si ocurre un error en la capa de servicios
     */
    public String getCityNameById(String cityId) throws ValidationException, BusinessException {
        if (cityId == null) {
            throw new ValidationException("El Id de la ciudad es requerido");
        }
        
        return cityService.getCityNameById(cityId);
    }
    
    /**
    * Validacion basica los datos de la ciudad antes de la inserción
    * @param cityDTO Datos de la ciudad a validar sus campos
    * @throws ValidationException Si algun dato no es válido
    */
    public void validateCityData(CityDTO cityDTO) throws ValidationException {
        String name = cityDTO.getCityName();
        String zipCode = cityDTO.getCityZIPCode();
        
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("El nombre de la ciudad es requerido");
        }
        
        if (zipCode == null || zipCode.trim().isEmpty()) {
            throw new ValidationException("El código postal es requerido");
        }
    }
}