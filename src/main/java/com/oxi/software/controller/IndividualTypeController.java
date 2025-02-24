package com.oxi.software.controller;

import com.oxi.software.business.IndividualTypeBusiness;
import com.oxi.software.dto.IndividualTypeDTO;
import com.oxi.software.utilities.exception.CustomException;
import com.oxi.software.utilities.http.ResponseHttpApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path =  "/api/v1/oxi/individual-type",
        method = RequestMethod.GET)
@CrossOrigin("*")
public class IndividualTypeController {

    private final IndividualTypeBusiness individualTypeBusiness;

    @Autowired
    public IndividualTypeController(IndividualTypeBusiness individualTypeBusiness) {
        this.individualTypeBusiness = individualTypeBusiness;
    }

    @GetMapping(path = "/all")
    public ResponseEntity<Map<String, Object>> getAllIndividualType() {
        try{
            List<IndividualTypeDTO> individualDTOSList = individualTypeBusiness.findAll();
            if (!individualDTOSList.isEmpty()) {
                return new ResponseEntity<>(ResponseHttpApi.responseHttpFindAll(
                        individualDTOSList,
                        HttpStatus.OK,
                        "Successfully Completed",
                        individualDTOSList.size()),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<>(ResponseHttpApi.responseHttpFindAll(
                        null,
                        HttpStatus.NO_CONTENT,
                        "Individuals not found",
                        0),
                        HttpStatus.ACCEPTED);
            }
        }catch (Exception e){
            throw new CustomException("Error getting individuals: " + e.getMessage(),
                    HttpStatus.CONFLICT);
        }
    }

}
