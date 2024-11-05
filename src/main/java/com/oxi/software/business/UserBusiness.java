package com.oxi.software.business;

import com.oxi.software.dto.UserDTO;
import com.oxi.software.entities.User;
import com.oxi.software.service.UserService;
import com.oxi.software.utilities.Util;
import com.oxi.software.utilities.exception.CustomException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.modelmapper.ModelMapper;

import java.util.Map;
import org.apache.log4j.Logger;

@Component
public class UserBusiness {

    @Autowired
    private UserService userService;

    // ModelMapper instance to convert to DTO
    private final ModelMapper modelMapper = new ModelMapper();

    // Logger instance
    private static final Logger logger = Logger.getLogger(UserBusiness.class);

    public void addUser(Map<String, Object> json) {
        try {
            System.out.println(json.toString());
            JSONObject dataObject = Util.getData(json);
            UserDTO environmentDTO = UserDTO.builder()
                    .id(0L)
                    .username(dataObject.get("").toString())
                    .password(dataObject.get("").toString())
                    // .headquarter(this.getHeadquarterDto(Long.parseLong(dataObject.get("idheadquarter").toString())))
                    // .coordination(this.getCoordinationDto(Long.parseLong(dataObject.get("idcoordination").toString())))
                    .build();

            User environment = modelMapper.map(environmentDTO, User.class);
            this.userService.save(environment);

        }catch (CustomException ce){
            logger.error(ce.getMessage());
            throw  new CustomException("Error to create user",ce.getStatus());
        }catch (Exception e){
            logger.error(e.getMessage());
            throw new RuntimeException();
        }
    }
}
