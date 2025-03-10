package com.oxi.software.business;

import com.oxi.software.dto.ProductDTO;
import com.oxi.software.dto.UnitDTO;
import com.oxi.software.entity.Product;
import com.oxi.software.entity.Unit;
import com.oxi.software.service.UnitService;
import com.oxi.software.utilities.exception.CustomException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UnitBusiness {
    
    
    private final UnitService unitService;
    private final ModelMapper modelMapper = new ModelMapper();

    public UnitBusiness(UnitService unitService) {
        this.unitService = unitService;
    }

    public List<UnitDTO> findAll(){
        try {
            List<Unit> unitList = this.unitService.findAll();
            if (unitList.isEmpty()) {
                return List.of();
            }
            return unitList.stream()
                    .map(unit -> modelMapper.map(unit, UnitDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new CustomException("Error getting unit: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
