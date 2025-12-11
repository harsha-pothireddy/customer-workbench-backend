package com.customersaas.workbench.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerInteractionDTO {

    private Long id;

    private String productId;

    private String customerId;

    private Integer customerRating;

    private String feedback;

    private LocalDateTime timestamp;

    private String responsesFromCustomerSupport;

    private String interactionType;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
