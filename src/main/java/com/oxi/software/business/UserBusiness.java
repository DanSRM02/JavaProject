package com.oxi.software.business;

import com.oxi.software.dto.ProductDTO;
import com.oxi.software.dto.UnitDTO;
import com.oxi.software.dto.UserDTO;
import com.oxi.software.entities.User;
import com.oxi.software.service.UserService;
import com.oxi.software.utilities.Util;
import com.oxi.software.utilities.exception.CustomException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.modelmapper.ModelMapper;

import java.util.Map;


@Component
public class UserBusiness {

    @Autowired
    private UserService userService;

    // ModelMapper instance to convert to DTO
    private final ModelMapper modelMapper = new ModelMapper();

    // Logger instance
    private static final Logger logger = LogManager.getLogger(UserBusiness.class);

    public ProductDTO validateData(Map<String, Object> data) throws CustomException {
        //Pass Map to JSONObject
        JSONObject request = Util.getData(data);

        //Prepare DTO
        ProductDTO productDTO = new ProductDTO();

        //Foreign key from the request
        Long productUnitId = request.getLong("unit_id");

        //Search the unit
//        UnitDTO unitDTO = this.getUnitDto(productUnitId);

        //Assign data to DTO
        productDTO.setId(0L);
        productDTO.setName(request.getString("name"));
        productDTO.setQuantity(request.getInt("quantity"));
        productDTO.setPrice(request.getInt("price"));
        productDTO.setState(request.getBoolean("state"));
//        productDTO.setUnit(unitDTO);
        return productDTO;
    }

    public void add(Map<String, Object> json) {
        try {

            JSONObject dataObject = Util.getData(json);

            UserDTO userDTO = UserDTO.builder()
                    .id(0L)
                    .username(dataObject.get("").toString())
                    .password(dataObject.get("").toString())
                    // .headquarter(this.getHeadquarterDto(Long.parseLong(dataObject.get("idheadquarter").toString())))
                    // .coordination(this.getCoordinationDto(Long.parseLong(dataObject.get("idcoordination").toString())))
                    .build();

            User environment = modelMapper.map(userDTO, User.class);
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
