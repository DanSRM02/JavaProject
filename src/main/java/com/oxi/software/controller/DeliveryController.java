package com.oxi.software.controller;

import com.oxi.software.business.DeliveryBusiness;
import com.oxi.software.dto.DeliveryDTO;
import com.oxi.software.utilities.exception.CustomException;
import com.oxi.software.utilities.http.ResponseHttpApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/v1/oxi/delivery", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT})
@CrossOrigin(origins = "*")
public class DeliveryController {

    private final DeliveryBusiness deliveryBusiness;

    @Autowired
    public DeliveryController(DeliveryBusiness deliveryBusiness) {
        this.deliveryBusiness = deliveryBusiness;
    }

    // Endpoint Add delivery
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> createDelivery(@RequestBody Map<String, Object> productMap) {
        try {
            // Call Business to create delivery
            deliveryBusiness.add(productMap);
            // Debug for errors
            return new ResponseEntity<>(ResponseHttpApi.responseHttpAction(
                    ResponseHttpApi.CODE_OK,
                    "Add Delivery successfully"),
                    HttpStatus.OK);
        } catch (CustomException e) {
            return new ResponseEntity<>(ResponseHttpApi.responseHttpAction(
                    ResponseHttpApi.CODE_BAD,
                    e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(ResponseHttpApi.responseHttpAction(
                    ResponseHttpApi.CODE_BAD,
                    "Error adding Delivery" + e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateDelivery(@RequestBody Map<String, Object> productMap, @PathVariable Long id) {
        try{
            deliveryBusiness.update(productMap, id);
            return new ResponseEntity<>(ResponseHttpApi.responseHttpAction(
                    ResponseHttpApi.CODE_OK,
                    "Update product successfully"),
                    HttpStatus.OK);
        }catch (CustomException e) {
            return new ResponseEntity<>(ResponseHttpApi.responseHttpAction(
                    ResponseHttpApi.CODE_BAD,
                    e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(ResponseHttpApi.responseHttpAction(
                    ResponseHttpApi.CODE_BAD,
                    "Error updated product" + e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllDeliverys() {
        try{
            List<DeliveryDTO> deliveryDTOSList = deliveryBusiness.findAll();
            if (!deliveryDTOSList.isEmpty()) {
                return new ResponseEntity<>(ResponseHttpApi.responseHttpFindAll(
                        deliveryDTOSList,
                        HttpStatus.OK,
                        "Successfully Completed",
                        deliveryDTOSList.size()),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<>(ResponseHttpApi.responseHttpFindAll(
                        null,
                        HttpStatus.NO_CONTENT,
                        "Deliverys not found",
                        0),
                        HttpStatus.ACCEPTED);
            }
        }catch (Exception e){
            throw new CustomException("Error getting deliverys: " + e.getMessage(),
                    HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<Map<String, Object>> getDeliveryById(@PathVariable Long id) {
        try{
            DeliveryDTO deliveryDTO = deliveryBusiness.findBy(id);
            if (deliveryDTO != null) {
                return new ResponseEntity<>(ResponseHttpApi.responseHttpFindId(
                        deliveryDTO,
                        ResponseHttpApi.CODE_OK,
                        "Successfully Completed"
                ), HttpStatus.OK);
            }
            return new ResponseEntity<>(ResponseHttpApi.responseHttpAction(
                    ResponseHttpApi.CODE_BAD,
                    "There isn't delivery"
            ), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            throw new CustomException("Error getting delivery: " + e.getMessage(),
                    HttpStatus.CONFLICT);
        }
    }

}