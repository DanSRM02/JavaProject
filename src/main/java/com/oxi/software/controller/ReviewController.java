package com.oxi.software.controller;

import com.oxi.software.business.ReviewBusiness;
import com.oxi.software.dto.ReviewDTO;
import com.oxi.software.dto.ReviewListDTO;
import com.oxi.software.utilities.exception.CustomException;
import com.oxi.software.utilities.http.ResponseHttpApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/v1/oxi/review", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT})
@CrossOrigin(origins = "*")
public class ReviewController {

    private final ReviewBusiness reviewBusiness;

    @Autowired
    public ReviewController(ReviewBusiness reviewBusiness) {
        this.reviewBusiness = reviewBusiness;
    }

    // Crear nueva reseña
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> createReview(@RequestBody Map<String, Object> reviewMap) {
        try {
            // Llamar al negocio para crear la reseña
            reviewBusiness.add(reviewMap);
            // Respuesta de éxito
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    "Review added successfully", HttpStatus.OK),
                    HttpStatus.OK);
        } catch (CustomException e) {
            // Respuesta para excepciones personalizadas
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Respuesta para excepciones generales
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    "Error adding review: " + e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        }
    }

    // Actualizar reseña
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateReview(@RequestBody Map<String, Object> reviewMap, @PathVariable Long id) {
        try {
            // Llamar al negocio para actualizar la reseña
            reviewBusiness.update(reviewMap, id);
            // Respuesta de éxito
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    "Review updated successfully", HttpStatus.OK),
                    HttpStatus.OK);
        } catch (CustomException e) {
            // Respuesta para excepciones personalizadas
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Respuesta para excepciones generales
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    "Error updating review: " + e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        }
    }

    // Obtener todas las reseñas
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllReviews() {
        try{
            List<ReviewListDTO> reviewDTOList = reviewBusiness.findAllReviews();
            if (!reviewDTOList.isEmpty()) {
                return new ResponseEntity<>(ResponseHttpApi.responseHttpFindAll(
                        reviewDTOList,
                        HttpStatus.OK,
                        "Successfully Completed",
                        reviewDTOList.size()),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<>(ResponseHttpApi.responseHttpFindAll(
                        null,
                        HttpStatus.NO_CONTENT,
                        "Reviews not found",
                        0),
                        HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            throw new CustomException("Error getting Reviews: " + e.getMessage(),
                    HttpStatus.CONFLICT);
        }
    }

    @PostMapping(path = "/deactivate/{id}")
    public ResponseEntity<Map<String, Object>> deactivateReview( @PathVariable Long id) {
        try {
            // Llamar al negocio para actualizar la reseña
            reviewBusiness.deactivate(id);
            // Respuesta de éxito
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    "Review updated successfully", HttpStatus.OK),
                    HttpStatus.OK);
        } catch (CustomException e) {
            // Respuesta para excepciones personalizadas
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Respuesta para excepciones generales
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    "Error deactivate review: " + e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        }
    }

    // Obtener reseña por ID
    @GetMapping("/find/{id}")
    public ResponseEntity<Map<String, Object>> getReviewById(@PathVariable Long id) {
        try{
            ReviewDTO reviewDTO = reviewBusiness.findBy(id);
            if (reviewDTO != null) {
                return new ResponseEntity<>(ResponseHttpApi.responseHttpFindId(
                        reviewDTO,
                        ResponseHttpApi.CODE_OK,
                        "Successfully Completed"
                ), HttpStatus.OK);
            }
            return new ResponseEntity<>(ResponseHttpApi.responseHttpAction(
                    ResponseHttpApi.CODE_BAD,
                    "There isn't that review"
            ), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            throw new CustomException("Error getting Review: " + e.getMessage(),
                    HttpStatus.CONFLICT);
        }
    }
}

