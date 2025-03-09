package com.oxi.software.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeliveryDetailsDTO {
    @JsonProperty("delivery_id")
    private Long deliveryId;
    @JsonProperty("delivery_state")
    private String deliveryState;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    // Order details
    @JsonProperty("order_id")
    private Long orderId;
    @JsonProperty("order_state")
    private String orderState;
    @JsonProperty("order_total")
    private Double orderTotal;
    private Boolean priority;

    // Client info
    @JsonProperty("client_name")
    private String clientName;
    @JsonProperty("client_address")
    private String clientAddress;
    @JsonProperty("client_phone")
    private String clientPhone;

    // Product details
    private List<ProductDetailDTO> products;

    @Builder
    @Getter
    public static class ProductDetailDTO {
        @JsonProperty("product_name")
        private String productName;
        @JsonProperty("quantity_ordered")
        private Integer quantityOrdered;
        @JsonProperty("unit_acronym")
        private String unitAcronym;
        @JsonProperty("unit_type")
        private String unitType;
        @JsonProperty("unit_price")
        private Double unitPrice;
    }
}