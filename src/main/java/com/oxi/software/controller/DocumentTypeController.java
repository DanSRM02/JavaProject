package com.oxi.software.controller;


import com.oxi.software.business.DocumentTypeBusiness;
import com.oxi.software.dto.DocumentTypeDTO;
import com.oxi.software.utilities.exception.CustomException;
import com.oxi.software.utilities.http.ResponseHttpApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path  = "/api/v1/oxi/document-type",
        method = RequestMethod.GET)
@CrossOrigin("*")
public class DocumentTypeController {

    private final DocumentTypeBusiness documentTypeBusiness;

    @Autowired
    public DocumentTypeController (DocumentTypeBusiness documentTypeBusiness) {
        this.documentTypeBusiness = documentTypeBusiness;
    }

    @GetMapping(path = "/all")
    public ResponseEntity<Map<String, Object>> getAllDocumentTypes() {
        try{
            List<DocumentTypeDTO> documentTypeDTOList = documentTypeBusiness.findAll();
            if (!documentTypeDTOList.isEmpty()) {
                return new ResponseEntity<>(ResponseHttpApi.responseHttpFindAll(
                        documentTypeDTOList,
                        HttpStatus.OK,
                        "Successfully Completed",
                        documentTypeDTOList.size()),
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
