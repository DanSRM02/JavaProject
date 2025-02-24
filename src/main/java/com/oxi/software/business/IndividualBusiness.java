package com.oxi.software.business;

import com.oxi.software.dto.*;
import com.oxi.software.entity.*;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class IndividualBusiness {
    private final IndividualTypeService individualTypeService;
    private final DocumentTypeService documentTypeService;
    private final IndividualService individualService;

    public IndividualBusiness(IndividualTypeService individualTypeService, DocumentTypeService documentTypeService, IndividualService individualService, UserBusiness userBusiness) {
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
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        long documentValue = request.getLong("document");
        String hashedDocument = passwordEncoder.encode(Long.toString(documentValue));
        individualDTO.setDocument(hashedDocument);

        //Assign data to DTO
        individualDTO.setId(0L);
        individualDTO.setName(request.getString("name"));
        individualDTO.setAddress(request.getString("address"));
        individualDTO.setPhone(request.getString("phone"));
        individualDTO.setEmail(request.getString("email"));

        //Search document type and assing to DTO
        Long documentTypeId = Long.parseLong(request.get("document_type_id").toString());
        DocumentTypeDTO documentTypeDTO = getDocumentTypeDTO(documentTypeId);
        individualDTO.setDocumentType(documentTypeDTO);

        //Search individual type and assing to DTO
        Long individualTypeId = Long.parseLong(request.get("individual_type_id").toString());
        IndividualTypeDTO individualTypeDTO = getIndividualTypeDTO(individualTypeId);
        individualDTO.setIndividualType(individualTypeDTO);

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


    public IndividualDTO add(Map<String, Object> request) {
        try {
            // Validar datos y convertir a DTO
            IndividualDTO individualDTO = validateData(request);

            // Mapear a entidad Individual
            Individual individual = modelMapper.map(individualDTO, Individual.class);

            // Asignar claves foráneas - Tipo de Documento
            individual.setDocumentType(documentTypeService.findBy(individualDTO.getDocumentType().getId()));

            // Asignar tipo de Individual
            individual.setIndividualType(individualTypeService.findBy(individualDTO.getIndividualType().getId()));

            // Guardar el individual y obtener la entidad persistida
            individual = this.individualService.save(individual);


            // Asegurarse de que se haya generado el ID y asignarlo al DTO
            if (individual.getId() == null || individual.getId() == 0L) {
                throw new CustomException("Error: el id generado es 0 o nulo", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            individualDTO = modelMapper.map(individual, IndividualDTO.class);
            individualDTO.setId(individual.getId()); // Asignar el id generado

            logger.info("Individual added successfully: {}", individual);
            return individualDTO;

        } catch (CustomException ce) {
            logger.error("Custom error: {}", ce.getMessage(), ce);
            throw new CustomException("Error adding individual", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Unexpected error occurred while adding individual", e);
            throw new RuntimeException("Unexpected error occurred while adding individual", e);
        }
    }

    public void update(Map<String, Object> request, Long id) {
        try {
            // Validar datos y convertir a DTO
            IndividualDTO individualDTO = validateData(request);
            individualDTO.setId(id);  // Establecer el ID del individual a actualizar

            // Crear la entidad Individual y asignar propiedades
            Individual individual = modelMapper.map(individualDTO, Individual.class);

            // Asignar claves foráneas - Tipo de Documento
            individual.setDocumentType(documentTypeService.findBy(individualDTO.getDocumentType().getId()));

            // Asignar tipo de Individual
            individual.setIndividualType(individualTypeService.findBy(individualDTO.getIndividualType().getId()));

            // Guardar el individual actualizado
            this.individualService.save(individual);

            // Log de información sobre la operación exitosa
            logger.info("Individual updated successfully: {}", individual);

        } catch (CustomException ce) {
            // Log de error personalizado y relanzamiento de la excepción
            logger.error("Custom error: {}", ce.getMessage(), ce);
            throw new CustomException("Error updating individual", HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (Exception e) {
            // Log de error inesperado y relanzamiento de la excepción
            logger.error("Unexpected error occurred while updating individual", e);
            throw new RuntimeException("Unexpected error occurred while updating individual", e);
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
