package com.oxi.software.controller;

import com.oxi.software.business.IndividualBusiness;
import com.oxi.software.dto.IndividualDTO;
import com.oxi.software.utilities.exception.CustomException;
import com.oxi.software.utilities.http.ResponseHttpApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(path = "/api/v1/oxi/individual", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT})
@CrossOrigin(origins = "*")
public class IndividualController {

    private final IndividualBusiness individualBusiness;

    @Autowired
    public IndividualController(IndividualBusiness individualBusiness) {
        this.individualBusiness = individualBusiness;
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> createIndividual(@RequestBody Map<String, Object> productMap) {
        try {
            // Call Business to create individual
            individualBusiness.add(productMap);
            // Success response
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    "Add Individual successfully", HttpStatus.OK),
                    HttpStatus.OK);
        } catch (CustomException e) {
            // Custom exception response
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // General exception response
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    "Error adding Individual: " + e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateIndividual(@RequestBody Map<String, Object> productMap, @PathVariable Long id) {
        try {
            // Call Business to update individual
            individualBusiness.update(productMap, id);
            // Success response
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    "Update Individual successfully", HttpStatus.OK),
                    HttpStatus.OK);
        } catch (CustomException e) {
            // Custom exception response
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // General exception response
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    "Error updating Individual: " + e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllIndividuals() {
        try{
            List<IndividualDTO> individualDTOSList = individualBusiness.findAll();
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

    @GetMapping("/find/{id}")
    public ResponseEntity<Map<String, Object>> getIndividualById(@PathVariable Long id) {
        try{
            IndividualDTO individualDTO = individualBusiness.findBy(id);
            if (individualDTO != null) {
                return new ResponseEntity<>(ResponseHttpApi.responseHttpFindId(
                        individualDTO,
                        ResponseHttpApi.CODE_OK,
                        "Successfully Completed"
                ), HttpStatus.OK);
            }
            return new ResponseEntity<>(ResponseHttpApi.responseHttpAction(
                    ResponseHttpApi.CODE_BAD,
                    "There isn't individual"
            ), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            throw new CustomException("Error getting individual: " + e.getMessage(),
                    HttpStatus.CONFLICT);
        }
    }
}
