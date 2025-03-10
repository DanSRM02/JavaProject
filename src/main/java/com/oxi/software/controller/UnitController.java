package com.oxi.software.controller;

import com.oxi.software.business.UnitBusiness;
import com.oxi.software.dto.DeliveryDTO;
import com.oxi.software.dto.UnitDTO;
import com.oxi.software.utilities.exception.CustomException;
import com.oxi.software.utilities.http.ResponseHttpApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/oxi/unit")
public class UnitController {

    @Autowired
    UnitBusiness unitBusiness;

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllUnits() {
        try{
            List<UnitDTO> unitDTOSList = unitBusiness.findAll();
            if (!unitDTOSList.isEmpty()) {
                return new ResponseEntity<>(ResponseHttpApi.responseHttpFindAll(
                        unitDTOSList,
                        HttpStatus.OK,
                        "Successfully Completed",
                        unitDTOSList.size()),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<>(ResponseHttpApi.responseHttpFindAll(
                        null,
                        HttpStatus.NO_CONTENT,
                        "Units not found",
                        0),
                        HttpStatus.ACCEPTED);
            }
        }catch (Exception e){
            throw new CustomException("Error getting units: " + e.getMessage(),
                    HttpStatus.CONFLICT);
        }
    }


}
