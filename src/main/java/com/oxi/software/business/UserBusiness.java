package com.oxi.software.business;

import com.oxi.software.dto.*;
import com.oxi.software.entities.Individual;
import com.oxi.software.entities.RolType;
import com.oxi.software.entities.User;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private final BCryptPasswordEncoder passwordEncoder; // Añadir BCryptPasswordEncoder

    @Autowired
    public UserBusiness(UserService userService, IndividualService individualService, RolTypeService rolTypeService, BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.individualService = individualService;
        this.rolTypeService = rolTypeService;
        this.passwordEncoder = passwordEncoder;
    }

    // ModelMapper instance to convert to DTO
    private final ModelMapper modelMapper = new ModelMapper();
    // Logger instance
    private static final Logger logger = LogManager.getLogger(UserBusiness.class);

    public UserDTO validateData(Map<String, Object> data) throws CustomException {
        JSONObject request = Util.getData(data);

        UserDTO userDTO = new UserDTO();

        // Validar y asignar datos obligatorios
        if (!request.has("username") || !request.has("password") || !request.has("rol_type_id")) {
            throw new CustomException("Missing required fields", HttpStatus.BAD_REQUEST);
        }

        userDTO.setUsername(request.getString("username"));
        userDTO.setPassword(request.getString("password"));
        userDTO.setState(request.optBoolean("state", true));

        // Validar y asignar RolType
        Long rolTypeId = request.has("rol_type_id") ? request.getLong("rol_type_id") : null;
        if (rolTypeId == null) {
            throw new CustomException("Role type ID is required", HttpStatus.BAD_REQUEST);
        }
        userDTO.setRolType(getRolTypeDTO(rolTypeId));

        // Validar y asignar Individual (opcional)
        Long individualId = request.has("individual_id") ? request.getLong("individual_id") : null;
        if (individualId != null) {
            userDTO.setIndividual(getIndividualDTO(individualId));
        }

        logger.debug("Validated UserDTO: {}", userDTO);
        return userDTO;
    }

    public void add(Map<String, Object> json) {
        try {
            // Validar datos y convertir a DTO
            UserDTO userDTO = validateData(json);

            // Crear la entidad User y asignar propiedades
            User user = modelMapper.map(userDTO, User.class);

            // Asignar claves foráneas si existen
            if (userDTO.getRolType() != null && userDTO.getRolType().getId() != null) {
                user.setRolType(rolTypeService.findBy(userDTO.getRolType().getId()));
            } else {
                throw new CustomException("Role type ID is null", HttpStatus.BAD_REQUEST);
            }

            if (userDTO.getIndividual() != null && userDTO.getIndividual().getId() != null) {
                Individual individual = individualService.findBy(userDTO.getIndividual().getId());
                if (individual == null) {
                    throw new CustomException("Individual not found", HttpStatus.BAD_REQUEST);
                }
                user.setIndividual(individual);
            } else {
                throw new CustomException("Individual ID is null", HttpStatus.BAD_REQUEST);
            }

            // Encriptar la contraseña antes de guardar
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

            // Guardar usuario
            this.userService.save(user);

            logger.info("User  added successfully: {}", user);

        } catch (CustomException ce) {
            logger.error("Custom error: {}", ce.getMessage(), ce);
            throw new CustomException("Error to create user", ce.getStatus());
        } catch (Exception e) {
            logger.error("Unexpected error occurred while adding user", e);
            throw new RuntimeException("Unexpected error occurred while adding user", e);
        }
    }

    public List<UserDTO> findAll() {
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
            logger.info("User  updated successfully: {}", user);

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

    public UserDTO authenticate(String email, String password) {
        User user = userService.findByEmail(email); // Asegúrate de que este método esté implementado
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return new UserDTO(user); // Retorna un nuevo UserDTO
        }
        return null; // Credenciales incorrectas
    }

    public UserDTO findBy(Long id) {
        try {
            User user = this.userService.findBy(id);
            logger.info("User  found: {}", user);
            return modelMapper.map(user, UserDTO.class);
        } catch (EntityNotFoundException eNT) {
            logger.error(eNT.getMessage());
            throw new CustomException("¡ERROR!, Not found user", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new CustomException("¡ERROR!, Error getting user by id", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public IndividualDTO getIndividualDTO(Long id) {
        Individual individual = individualService.findBy(id);
        return modelMapper.map(individual, IndividualDTO.class);
    }

    public RolTypeDTO getRolTypeDTO(Long id) {
        RolType roleType = rolTypeService.findBy(id);
        return modelMapper.map(roleType, RolTypeDTO.class);
    }
}