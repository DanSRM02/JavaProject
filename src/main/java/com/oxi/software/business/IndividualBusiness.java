package com.oxi.software.business;

import com.oxi.software.dto.*;
import com.oxi.software.entities.*;
import com.oxi.software.service.DocumentTypeService;
import com.oxi.software.service.IndividualService;
import com.oxi.software.service.IndividualTypeService;
import com.oxi.software.utilities.Util;
import com.oxi.software.utilities.exception.CustomException;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class IndividualBusiness {
    private final IndividualTypeService individualTypeService;
    private final DocumentTypeService documentTypeService;
    private final IndividualService individualService;

    public IndividualBusiness(IndividualTypeService individualTypeService, DocumentTypeService documentTypeService, IndividualService individualService) {
        this.individualTypeService = individualTypeService;
        this.documentTypeService = documentTypeService;
        this.individualService = individualService;
    }

    private static final Logger logger = LogManager.getLogger(Util.class);
    private final ModelMapper modelMapper = new ModelMapper();

    public IndividualDTO validateData(Map<String, Object> data) throws CustomException {
        //Pass Map to JSONObject
        JSONObject request = Util.getData(data);
        //Prepare DTO
        IndividualDTO individualDTO = new IndividualDTO();
        logger.debug(individualDTO.toString());
        System.out.println(individualDTO);

        //Assign data to DTO
        individualDTO.setId(0L);
        individualDTO.setName(request.getString("name"));
        individualDTO.setAddress(request.getString("address"));
        individualDTO.setPhone(request.getString("phone"));
        individualDTO.setDocument(request.getLong("document"));
        individualDTO.setEmail(request.getString("email"));

        //Search document type and assing to DTO
        Long documentTypeId = Long.parseLong(request.get("document_type_id").toString());
        DocumentTypeDTO documentTypeDTO = getDocumentTypeDTO(documentTypeId);
        individualDTO.setDocumentType(documentTypeDTO);

        //Search individual type and assing to DTO
        Long individualTypeId = Long.parseLong(request.get("individual_type_id").toString());
        IndividualTypeDTO individualTypeDTO = getIndividualTypeDTO(individualTypeId);
        individualDTO.setIndividualType(individualTypeDTO);

        System.out.println(individualDTO);
        return individualDTO;
    }

    public List<IndividualDTO> findAll(){
        try {
            List<Individual> productList = this.individualService.findAll();
            if (productList.isEmpty()) {
                return List.of();
            }
            return productList.stream()
                    .map(individual -> modelMapper.map(individual, IndividualDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new CustomException("Error getting individual: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    public IndividualDTO findBy(Long id) {
        try {
            Individual individual = this.individualService.findBy(id);
            logger.info("individual find {}", individual);
            return modelMapper.map(individual, IndividualDTO.class);

        } catch (EntityNotFoundException eNT) {
            logger.error(eNT.getMessage());
            throw new CustomException("¡ERROR!, Not found individual", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new CustomException("¡ERROR!, Error getting individual by id", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    public void add(Map<String, Object> request) {
        try{
            //Build the individual using from DTO
            IndividualDTO individualDTO = validateData(request);
            Individual individual = modelMapper.map(individualDTO, Individual.class);
            //Assign Foreign Keys - Document Type
            individual.setDocumentType(documentTypeService.findBy(individualDTO.getDocumentType().getId()));
            //Individual Type
            individual.setIndividualType(individualTypeService.findBy(individualDTO.getIndividualType().getId()));

            this.individualService.save(individual);
        } catch (CustomException ce){
            logger.error(ce.getMessage());
            throw new CustomException("Error adding individual", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException();
        }

    }

    public void update(Map<String, Object> request, Long id) {
        try{
            //Validate Data to DTO
            IndividualDTO individualDTO = validateData(request);
            individualDTO.setId(id);

            Individual individual = modelMapper.map(individualDTO, Individual.class);
            //Assign Foreign Keys - Document Type
            individual.setDocumentType(documentTypeService.findBy(individualDTO.getDocumentType().getId()));
            //Individual Type
            individual.setIndividualType(individualTypeService.findBy(individualDTO.getIndividualType().getId()));
            this.individualService.save(individual);
        } catch (CustomException ce){
            logger.error(ce.getMessage());
            throw new CustomException("Error add product", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException();
        }

    }

    public IndividualTypeDTO getIndividualTypeDTO(Long id){
        IndividualType individualType = individualTypeService.findBy(id);
        return modelMapper.map(individualType, IndividualTypeDTO.class);
    }

    public DocumentTypeDTO getDocumentTypeDTO(Long id){
        DocumentType documentType = documentTypeService.findBy(id);
        return modelMapper.map(documentType, DocumentTypeDTO.class);
    }

}
