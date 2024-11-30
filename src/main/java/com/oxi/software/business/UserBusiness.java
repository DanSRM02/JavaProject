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
import org.springframework.stereotype.Component;
import org.modelmapper.ModelMapper;

import java.util.Map;


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
            UserDTO userDTO = validateData(json);
            User user = modelMapper.map(userDTO, User.class);
            //Assign Foreign Keys - Role Type
            user.setRolType(rolTypeService.findBy(userDTO.getRolType().getId()));
            //Individual Type
            user.setIndividual(individualService.findBy(userDTO.getIndividual().getId()));
            this.userService.save(user);
        }catch (CustomException ce){
            logger.error(ce.getMessage());
            throw  new CustomException("Error to create user",ce.getStatus());
        }catch (Exception e){
            logger.error(e.getMessage());
            throw new RuntimeException();
        }
    }

    public void update(Map<String, Object> json, Long id) {
        try {
            UserDTO userDTO = validateData(json);
            userDTO.setId(id);
            User user = modelMapper.map(userDTO, User.class);
            //Assign Foreign Keys - Role Type
            user.setRolType(rolTypeService.findBy(userDTO.getRolType().getId()));
            //Individual Type
            user.setIndividual(individualService.findBy(userDTO.getIndividual().getId()));
            this.userService.save(user);
        }catch (CustomException ce){
            logger.error(ce.getMessage());
            throw  new CustomException("Error to modified user",ce.getStatus());
        }catch (Exception e){
            logger.error(e.getMessage());
            throw new RuntimeException();
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
