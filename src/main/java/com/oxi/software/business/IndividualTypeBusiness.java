package com.oxi.software.business;

import com.oxi.software.dto.IndividualDTO;
import com.oxi.software.dto.IndividualTypeDTO;
import com.oxi.software.entity.Individual;
import com.oxi.software.entity.IndividualType;
import com.oxi.software.service.IndividualTypeService;
import com.oxi.software.utilities.exception.CustomException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class IndividualTypeBusiness {

    private final IndividualTypeService individualTypeService;
    private final ModelMapper modelMapper = new ModelMapper();

    public IndividualTypeBusiness(IndividualTypeService individualTypeService) {
        this.individualTypeService = individualTypeService;
    }

    public List<IndividualTypeDTO> findAll(){
        try {
            List<IndividualType> individualTypeList = this.individualTypeService.findAll();
            if (individualTypeList.isEmpty()) {
                return List.of();
            }
            return individualTypeList.stream()
                    .map(individualType -> modelMapper.map(individualType, IndividualTypeDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new CustomException("Error getting individual type: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
