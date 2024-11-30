package com.oxi.software.business;

import com.oxi.software.dto.ProductDTO;
import com.oxi.software.dto.UnitDTO;
import com.oxi.software.entities.Product;
import com.oxi.software.entities.Unit;
import com.oxi.software.service.ProductService;
import com.oxi.software.service.UnitService;
import com.oxi.software.utilities.Util;
import com.oxi.software.utilities.exception.CustomException;
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
public class ProductBusiness {

    private final ProductService productService;
    private final UnitService unitService;

    private static final Logger logger = LogManager.getLogger(Util.class);
    private final ModelMapper modelMapper = new ModelMapper();

    public ProductBusiness(ProductService productService, UnitService unitService) {
        this.productService = productService;
        this.unitService = unitService;
    }

    public ProductDTO validateData(Map<String, Object> data) throws CustomException {
        //Pass Map to JSONObject
        JSONObject request = Util.getData(data);

        //Prepare DTO
        ProductDTO productDTO = new ProductDTO();

        //Foreign key from the request
        Long productUnitId = request.getLong("unit_id");

        //Search the unit
        UnitDTO unitDTO = this.getUnitDto(productUnitId);

        //Assign data to DTO
        productDTO.setId(0L);
        productDTO.setName(request.getString("name"));
        productDTO.setQuantity(request.getInt("quantity"));
        productDTO.setPrice(request.getInt("price"));
        productDTO.setState(request.getBoolean("state"));
        productDTO.setUnit(unitDTO);
        return productDTO;
    }

    public List<ProductDTO> findAll(){
        try {
            List<Product> productList = this.productService.findAll();
            if (productList.isEmpty()) {
                return List.of();
            }
            return productList.stream()
                    .map(journey -> modelMapper.map(journey, ProductDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new CustomException("Error getting product: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    public ProductDTO findBy(Long id) {
        try{
            Product productDTO = this.productService.findBy(id);
            return modelMapper.map(productDTO, ProductDTO.class);
        } catch (CustomException ce){
            logger.error(ce.getMessage());
            throw new CustomException("Error found product", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException();
        }
    }

    public void add(Map<String, Object> request) {
        try{
            //Build the product using annotation builder from DTO
            ProductDTO productDTO = validateData(request);
            Product product = modelMapper.map(productDTO, Product.class);
            product.setUnit(unitService.findBy(productDTO.getUnit().getId()));
            this.productService.save(product);
        } catch (CustomException ce){
            logger.error(ce.getMessage());
            throw new CustomException("Error add product", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException();
        }

    }

    public void update(Map<String, Object> request, Long id) {
        try{
            //Validate Data to DTO
            ProductDTO productDTO = validateData(request);
            productDTO.setId(id);

            //Search Product and save the product
            Product product = modelMapper.map(productDTO, Product.class);
            product.setUnit(unitService.findBy(productDTO.getUnit().getId()));
            this.productService.save(product);
        } catch (CustomException ce){
            logger.error(ce.getMessage());
            throw new CustomException("Error add product", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException();
        }

    }

    private UnitDTO getUnitDto(Long id){
        Unit unit = unitService.findBy(id);
        return modelMapper.map(unit, UnitDTO.class);
    }

}
