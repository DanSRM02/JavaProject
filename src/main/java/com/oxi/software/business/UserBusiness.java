package com.oxi.software.business;

import com.oxi.software.dto.*;
import com.oxi.software.entity.Individual;
import com.oxi.software.entity.RolType;
import com.oxi.software.entity.User;
import com.oxi.software.service.IndividualService;
import com.oxi.software.service.RolTypeService;
import com.oxi.software.service.UserService;
import com.oxi.software.utilities.Util;
import com.oxi.software.utilities.exception.CustomException;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
public class UserBusiness {

    private final UserService userService;
    private final IndividualService individualService;
    private final RolTypeService rolTypeService;

    @Autowired
    public UserBusiness(UserService userService, IndividualService individualService, RolTypeService rolTypeService) {
        this.userService = userService;
        this.individualService = individualService;
        this.rolTypeService = rolTypeService;
    }

    // ModelMapper instance to convert to DTO
    private final ModelMapper modelMapper = new ModelMapper();
    // Logger instance
    private static final Logger logger = LogManager.getLogger(UserBusiness.class);

    public UserDTO validateData(Map<String, Object> data) throws CustomException {
        //Pass Map to JSONObject
        JSONObject request = Util.getData(data);
        
        //Prepare DTO
        UserDTO userDTO = new UserDTO();
        logger.debug(userDTO.toString());
        System.out.println(userDTO);

        //Assign data to DTO
        userDTO.setId(0L);
        userDTO.setUsername(request.getString("name"));
        userDTO.setState(request.getBoolean("state"));
        userDTO.setPassword(request.getString("password"));

        //Search role type and assing to DTO
        Long rolTypeId = Long.parseLong(request.get("rol_type_id").toString());
        RolTypeDTO rolTypeDTO = getRolTypeDTO(rolTypeId);
        userDTO.setRolType(rolTypeDTO);

        //Search individual type and assing to DTO
        Long individualId = Long.parseLong(request.get("individual_id").toString());
        IndividualDTO individualDTO = getIndividualDTO(individualId);
        userDTO.setIndividual(individualDTO);

        System.out.println(userDTO);
        return userDTO;
    }

    public void add(Map<String, Object> json) {
        try {
            // Validar datos y convertir a DTO
            UserDTO userDTO = validateData(json);

            // Crear la entidad User y asignar propiedades
            User user = modelMapper.map(userDTO, User.class);

            // Asignar claves foráneas
            user.setRolType(rolTypeService.findBy(userDTO.getRolType().getId()));
            user.setIndividual(individualService.findBy(userDTO.getIndividual().getId()));

            // Guardar usuario
            this.userService.save(user);

            // Log información sobre la operación exitosa
            logger.info("User added successfully: {}", user);

        } catch (CustomException ce) {
            // Log de error personalizado y relanzamiento de la excepción
            logger.error("Custom error: {}", ce.getMessage(), ce);
            throw new CustomException("Error to create user", ce.getStatus());
        } catch (Exception e) {
            // Log de error inesperado y relanzamiento de la excepción
            logger.error("Unexpected error occurred while adding user", e);
            throw new RuntimeException("Unexpected error occurred while adding user", e);
        }
    }

    public List<UserDTO> findAll(){
        try {
            List<User> userList = this.userService.findAll();
            if (userList.isEmpty()) {
                return List.of();
            }
            return userList.stream()
                    .map(user -> modelMapper.map(user, UserDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new CustomException("Error getting users: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    public void update(Map<String, Object> json, Long id) {
        try {
            // Validar datos y convertir a DTO
            UserDTO userDTO = validateData(json);
            userDTO.setId(id);  // Establecer el ID del usuario a actualizar

            // Crear la entidad User y asignar propiedades
            User user = modelMapper.map(userDTO, User.class);

            // Asignar claves foráneas
            user.setRolType(rolTypeService.findBy(userDTO.getRolType().getId()));
            user.setIndividual(individualService.findBy(userDTO.getIndividual().getId()));

            // Guardar usuario modificado
            this.userService.save(user);

            // Log información sobre la operación exitosa
            logger.info("User updated successfully: {}", user);

        } catch (CustomException ce) {
            // Log de error personalizado y relanzamiento de la excepción
            logger.error("Custom error: {}", ce.getMessage(), ce);
            throw new CustomException("Error to modify user", ce.getStatus());
        } catch (Exception e) {
            // Log de error inesperado y relanzamiento de la excepción
            logger.error("Unexpected error occurred while updating user", e);
            throw new RuntimeException("Unexpected error occurred while updating user", e);
        }
    }

    public UserDTO findBy(Long id){
        try {
            User user = this.userService.findBy(id);
            logger.info("individual find {}", user);
            return modelMapper.map(user, UserDTO.class);
        } catch (EntityNotFoundException eNT) {
            logger.error(eNT.getMessage());
            throw new CustomException("¡ERROR!, Not found user", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new CustomException("¡ERROR!, Error getting user by id", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public IndividualDTO getIndividualDTO(Long id){
        Individual individual = individualService.findBy(id);
        return modelMapper.map(individual, IndividualDTO.class);
    }
    
    public RolTypeDTO getRolTypeDTO(Long id){
        RolType roleType = rolTypeService.findBy(id);
        return modelMapper.map(roleType, RolTypeDTO.class);
    }
}
