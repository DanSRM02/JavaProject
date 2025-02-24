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
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserBusiness(UserService userService, IndividualService individualService, RolTypeService rolTypeService, BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.individualService = individualService;
        this.rolTypeService = rolTypeService;
        this.passwordEncoder = passwordEncoder;

    }
    public User findByUsername(String username) {
        return userService.findByUsername(username);
    }

    public void update(Map<String, Object> json, Long id) {
        try {
            User user = userService.findBy(id);
            if (user == null) {
                throw new CustomException("User not found", HttpStatus.NOT_FOUND);
            }
            UserDTO userDTO = validateData(json);
            user.setUsername(userDTO.getUsername());
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            user.setRolType(rolTypeService.findBy(userDTO.getRolType().getId()));
            user.setState(userDTO.getState());
            userService.save(user);
            logger.info("User updated successfully: {}", user);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error updating user", e);
        }
    }


    private final ModelMapper modelMapper = new ModelMapper();
    private static final Logger logger = LogManager.getLogger(UserBusiness.class);


    public UserDTO authenticate(String username, String password) {
        logger.debug("🔹 Recibida petición de autenticación. Username: {}, Password: {}", username, password);

        if (username == null || username.isBlank()) {
            logger.error("❌ Username es nulo o vacío");
            throw new CustomException("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }

        logger.debug("🔹 Buscando usuario con email: {}", username);
        User user = userService.findByUsername(username);


        if (user == null) {
            logger.error("❌ Usuario no encontrado con email: {}", username);
            throw new CustomException("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }

        logger.debug("✅ Usuario encontrado: {}", user.getUsername());

        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.error("❌ Contraseña incorrecta");
            throw new CustomException("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }

        logger.debug("✅ Contraseña válida, generando DTO");
        return modelMapper.map(user, UserDTO.class);
    }




    public UserDTO validateData(Map<String, Object> data) throws CustomException {
        logger.debug("🔹 Validando datos de usuario: {}", data);
        JSONObject request = Util.getData(data);
        UserDTO userDTO = new UserDTO();

        if (!request.has("username") || !request.has("password") || !request.has("rol_type_id")) {
            logger.error("❌ Faltan campos requeridos en la petición");
            throw new CustomException("Missing required fields", HttpStatus.BAD_REQUEST);
        }

        userDTO.setUsername(request.getString("username"));
        userDTO.setPassword(request.getString("password"));
        userDTO.setState(request.optBoolean("state", true));

        Long rolTypeId = request.has("rol_type_id") ? request.getLong("rol_type_id") : null;
        if (rolTypeId == null) {
            logger.error("❌ RolTypeID es requerido");
            throw new CustomException("Role type ID is required", HttpStatus.BAD_REQUEST);
        }
        userDTO.setRolType(getRolTypeDTO(rolTypeId));

        Long individualId = request.has("individual_id") ? request.getLong("individual_id") : null;
        if (individualId != null) {
            userDTO.setIndividual(getIndividualDTO(individualId));
        }

        logger.debug("✅ Datos validados: {}", userDTO);
        return userDTO;
    }



    public void add(Map<String, Object> json) {
        try {
            UserDTO userDTO = validateData(json);
            User user = modelMapper.map(userDTO, User.class);
            user.setRolType(rolTypeService.findBy(userDTO.getRolType().getId()));
            user.setIndividual(individualService.findBy(userDTO.getIndividual().getId()));
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            this.userService.save(user);
            logger.info("User added successfully: {}", user);
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
            throw new CustomException("Error getting users: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public UserDTO findBy(Long id) {
        try {
            User user = this.userService.findBy(id);
            logger.info("User found: {}", user);
            return modelMapper.map(user, UserDTO.class);
        } catch (EntityNotFoundException eNT) {
            logger.error(eNT.getMessage());
            throw new CustomException("Not found user", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new CustomException("Error getting user by id", HttpStatus.INTERNAL_SERVER_ERROR);
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

    public User findByEmail(String email) {
        return userService.findByEmail(email);
    }
}
