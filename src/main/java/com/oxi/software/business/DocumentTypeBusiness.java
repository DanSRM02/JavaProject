package com.oxi.software.business;

import com.oxi.software.dto.DocumentTypeDTO;
import com.oxi.software.dto.IndividualDTO;
import com.oxi.software.dto.IndividualTypeDTO;
import com.oxi.software.entity.DocumentType;
import com.oxi.software.entity.Individual;
import com.oxi.software.entity.IndividualType;
import com.oxi.software.service.DocumentTypeService;
import com.oxi.software.service.IndividualTypeService;
import com.oxi.software.utilities.exception.CustomException;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DocumentTypeBusiness {
    
    private final DocumentTypeService documentTypeService;
    private final ModelMapper modelMapper = new ModelMapper();

    public DocumentTypeBusiness(DocumentTypeService documentTypeService) {
        this.documentTypeService = documentTypeService;
    }

    public List<DocumentTypeDTO> findAll(){
        try {
            List<DocumentType> documentTypeList = this.documentTypeService.findAll();
            if (documentTypeList.isEmpty()) {
                return List.of();
            }
            return documentTypeList.stream()
                    .map(documentType -> modelMapper.map(documentType, DocumentTypeDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new CustomException("Error getting document type: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
