package com.customersaas.workbench.service;

import com.customersaas.workbench.dto.CustomerInteractionDTO;
import com.customersaas.workbench.dto.SearchResultDTO;
import com.customersaas.workbench.entity.CustomerInteraction;
import com.customersaas.workbench.repository.CustomerInteractionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerInteractionService {

    private final CustomerInteractionRepository repository;

    public CustomerInteraction save(CustomerInteraction interaction) {
        return repository.save(interaction);
    }

    public SearchResultDTO search(
            String customerId,
            String interactionType,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable) {

        Page<CustomerInteraction> result = repository.searchInteractions(
            customerId,
            interactionType,
            startDate,
            endDate,
            pageable
        );

        Page<CustomerInteractionDTO> dtoPage = result.map(this::toDTO);
        return SearchResultDTO.fromPage(dtoPage);
    }

    private CustomerInteractionDTO toDTO(CustomerInteraction interaction) {
        return CustomerInteractionDTO.builder()
            .id(interaction.getId())
            .productId(interaction.getProductId())
            .customerId(interaction.getCustomerId())
            .customerRating(interaction.getCustomerRating())
            .feedback(interaction.getFeedback())
            .timestamp(interaction.getTimestamp())
            .responsesFromCustomerSupport(interaction.getResponsesFromCustomerSupport())
            .interactionType(interaction.getInteractionType())
            .createdAt(interaction.getCreatedAt())
            .updatedAt(interaction.getUpdatedAt())
            .build();
    }
}
