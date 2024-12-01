package com.oxi.software.controller;

import com.oxi.software.business.PurchaseBusiness;
import com.oxi.software.dto.PurchaseDTO;
import com.oxi.software.utilities.exception.CustomException;
import com.oxi.software.utilities.http.ResponseHttpApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/v1/oxi/purchase", method = { RequestMethod.PUT, RequestMethod.GET,RequestMethod.DELETE, RequestMethod.POST})
@CrossOrigin(origins = "*")
public class PurchaseController {

    private final PurchaseBusiness purchaseBusiness;

    @Autowired
    public PurchaseController(PurchaseBusiness purchaseBusiness) {
        this.purchaseBusiness = purchaseBusiness;
    }

    // Endpoint Add Purchase
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> createPurchase(@RequestBody Map<String, Object> productMap) {
        try {
            // Call Business to create Purchase
            purchaseBusiness.add(productMap);

            // Success response
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    "Add purchase successfully", HttpStatus.OK),
                    HttpStatus.OK);
        } catch (CustomException e) {
            // Custom exception response
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // General exception response
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    "Error adding Purchase: " + e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updatePurchase(@RequestBody Map<String, Object> productMap, @PathVariable Long id) {
        try {
            // Call Business to update Purchase
            purchaseBusiness.update(productMap, id);

            // Success response
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    "Update purchase successfully", HttpStatus.OK),
                    HttpStatus.OK);
        } catch (CustomException e) {
            // Custom exception response
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // General exception response
            return new ResponseEntity<>(ResponseHttpApi.responseHttpPost(
                    "Error updating purchase: " + e.getMessage(), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllPurchases() {
        try{
            List<PurchaseDTO> productDTOList = purchaseBusiness.findAll();
            if (!productDTOList.isEmpty()) {
                return new ResponseEntity<>(ResponseHttpApi.responseHttpFindAll(
                        productDTOList,
                        HttpStatus.OK,
                        "Successfully Completed",
                        productDTOList.size()),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<>(ResponseHttpApi.responseHttpFindAll(
                        null,
                        HttpStatus.NO_CONTENT,
                        "Purchases not found",
                        0),
                        HttpStatus.ACCEPTED);
            }
        }catch (Exception e){
            throw new CustomException("Error getting Purchases: " + e.getMessage(),
                    HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<Map<String, Object>> getPurchaseById(@PathVariable Long id) {
        try{
            PurchaseDTO productDTO = purchaseBusiness.findBy(id);
            if (productDTO != null) {
                return new ResponseEntity<>(ResponseHttpApi.responseHttpFindId(
                        productDTO,
                        ResponseHttpApi.CODE_OK,
                        "Successfully Completed"
                ), HttpStatus.OK);
            }
            return new ResponseEntity<>(ResponseHttpApi.responseHttpAction(
                    ResponseHttpApi.CODE_BAD,
                    "There isn't that product"
            ), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            throw new CustomException("Error getting Purchase: " + e.getMessage(),
                    HttpStatus.CONFLICT);
        }
    }
}
