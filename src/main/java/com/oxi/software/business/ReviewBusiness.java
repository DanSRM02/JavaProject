package com.oxi.software.business;

import com.oxi.software.dto.ProductDTO;
import com.oxi.software.dto.ReviewDTO;
import com.oxi.software.dto.ReviewListDTO;
import com.oxi.software.dto.UserDTO;
import com.oxi.software.entity.Product;
import com.oxi.software.entity.Review;
import com.oxi.software.entity.User;
import com.oxi.software.repository.projection.ReviewListProjection;
import com.oxi.software.service.ProductService;
import com.oxi.software.service.ReviewService;
import com.oxi.software.service.UserService;
import com.oxi.software.utilities.types.Util;
import com.oxi.software.utilities.exception.CustomException;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.oxi.software.utilities.types.Util.getData;

@Component
public class ReviewBusiness {

    private final ReviewService reviewService;
    private final ProductService productService;
    private final UserService userService;

    @Autowired
    public ReviewBusiness(ReviewService reviewService, ProductService productService, UserService userService) {
        this.reviewService = reviewService;
        this.productService = productService;
        this.userService = userService;
    }

    private final ModelMapper modelMapper = new ModelMapper();
    private static final Logger logger = LogManager.getLogger(Util.class);

    // Crear una nueva reseña
    public void add(Map<String, Object> request) {
        try {
            ReviewDTO reviewDTO = validateData(request);

            // Mapeo manual para evitar problemas
            Review review = new Review();
            review.setTitle(reviewDTO.getTitle());
            review.setDescription(reviewDTO.getDescription());
            review.setRating(reviewDTO.getRating());
            review.setState(reviewDTO.isState());

            // Asignar relaciones directamente desde los servicios
            Product product = productService.findBy(reviewDTO.getProduct().getId());
            User user = userService.findBy(reviewDTO.getUser().getId());

            review.setProduct(product);
            review.setUser(user);

            reviewService.save(review);

        }  catch (CustomException ce) {
            logger.error("Custom exception while adding review: {}", ce.getMessage(), ce);
            throw new CustomException("Error creating review: " + ce.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Unexpected error while adding review: {}", e.getMessage(), e);
            throw new RuntimeException("Unexpected error occurred while adding review", e);
        }
    }

    // Actualizar una reseña existente
    public void update(Map<String, Object> request, Long id) {
        try {
            logger.debug("Starting to update review with id: {}", id);

            // Validar y mapear el DTO
            ReviewDTO reviewDTO = validateData(request);
            reviewDTO.setId(id);
            logger.debug("Validated ReviewDTO: {}", reviewDTO);

            Review review = modelMapper.map(reviewDTO, Review.class);
            logger.debug("Mapped Review entity: {}", review);

            // Verificar la existencia del producto
            Product product = productService.findBy(reviewDTO.getProduct().getId());
            if (product == null) {
                logger.error("Product not found for id: {}", reviewDTO.getProduct().getId());
                throw new CustomException("Product not found", HttpStatus.NOT_FOUND);
            }
            review.setProduct(product);
            logger.debug("Assigned product to review: {}", product);

            // Verificar la existencia del usuario
            User user = userService.findBy(reviewDTO.getUser().getId());
            if (user == null) {
                logger.error("User not found for id: {}", reviewDTO.getUser().getId());
                throw new CustomException("User not found", HttpStatus.NOT_FOUND);
            }
            review.setUser(user);
            logger.debug("Assigned user to review: {}", user);

            // Guardar la reseña actualizada en la base de datos
            reviewService.save(review);
            logger.info("Review successfully updated: {}", review);

        } catch (CustomException ce) {
            logger.error("Custom exception while updating review: {}", ce.getMessage(), ce);
            throw new CustomException("Error updating review: " + ce.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Unexpected error while updating review: {}", e.getMessage(), e);
            throw new RuntimeException("Unexpected error occurred while updating review", e);
        }
    }

    public ReviewDTO findBy(Long id) {
        try {
            logger.debug("Fetching review by id: {}", id);

            Review review = this.reviewService.findBy(id);
            logger.info("Found review: {}", review);

            return modelMapper.map(review, ReviewDTO.class);

        } catch (EntityNotFoundException eNT) {
            logger.error("Entity not found exception: {}", eNT.getMessage(), eNT);
            throw new CustomException("¡ERROR!, Not found review", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error getting review by id: {}", e.getMessage(), e);
            throw new CustomException("¡ERROR!, Error getting review by id", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<ReviewListDTO> findAllReviews(){
        try {
            logger.debug("Fetching all reviews from the database");

            List<ReviewListProjection> reviewListProjections = reviewService.findAllProjection();
            logger.debug("Found reviews: {}", reviewListProjections);
            
            return reviewListProjections.stream()
                    .map(review -> modelMapper.map(review, ReviewListDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error retrieving reviews: {}", e.getMessage(), e);
            throw new CustomException("Error retrieving reviews: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ReviewDTO validateData(Map<String, Object> request) {
        logger.debug("Validating request data: {}", request);

        JSONObject data = getData(request);
        logger.debug("Parsed data from request: {}", data);

        ReviewDTO reviewDTO = new ReviewDTO();

        reviewDTO.setId(0L);
        reviewDTO.setTitle(data.getString("title"));
        reviewDTO.setDescription(data.getString("description"));
        reviewDTO.setRating(data.getInt("rating"));
        reviewDTO.setState(data.getBoolean("state"));
        logger.debug("Mapped ReviewDTO title and description: {}, {}", reviewDTO.getTitle(), reviewDTO.getDescription());

        // Assign Foreign Keys
        Long productId = Long.parseLong(data.get("product_id").toString());
        ProductDTO product = modelMapper.map(productService.findBy(productId), ProductDTO.class);
        reviewDTO.setProduct(product);
        logger.debug("Assigned product DTO: {}", product);

        Long userId = Long.parseLong(data.get("user_id").toString());
        UserDTO user = modelMapper.map(userService.findBy(userId), UserDTO.class);
        reviewDTO.setUser(user);
        logger.debug("Assigned user DTO: {}", user);

        return reviewDTO;
    }
}

