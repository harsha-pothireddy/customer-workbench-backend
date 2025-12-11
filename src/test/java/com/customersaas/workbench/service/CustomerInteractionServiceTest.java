package com.customersaas.workbench.service;

import com.customersaas.workbench.entity.CustomerInteraction;
import com.customersaas.workbench.repository.CustomerInteractionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerInteractionServiceTest {

    @Mock
    private CustomerInteractionRepository repository;

    @InjectMocks
    private CustomerInteractionService service;

    private CustomerInteraction testInteraction;

    @BeforeEach
    void setUp() {
        testInteraction = CustomerInteraction.builder()
            .id(1L)
            .productId("PROD-001")
            .customerId("CUST-001")
            .customerRating(5)
            .feedback("Great service!")
            .timestamp(LocalDateTime.now())
            .responsesFromCustomerSupport("Thank you!")
            .interactionType("email")
            .build();
    }

    @Test
    void testSaveInteraction() {
        when(repository.save(any(CustomerInteraction.class))).thenReturn(testInteraction);

        CustomerInteraction saved = service.save(testInteraction);

        assertNotNull(saved);
        assertEquals("PROD-001", saved.getProductId());
        assertEquals("CUST-001", saved.getCustomerId());
    }

    @Test
    void testSearchInteractions() {
        List<CustomerInteraction> interactions = new ArrayList<>();
        interactions.add(testInteraction);
        Page<CustomerInteraction> page = new PageImpl<>(interactions, PageRequest.of(0, 10), 1);

        when(repository.searchInteractions(
            anyString(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)
        )).thenReturn(page);

        var result = service.search("CUST-001", "email", null, null, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getInteractions().size());
        assertEquals("CUST-001", result.getInteractions().get(0).getCustomerId());
    }

    @Test
    void testSearchWithNullFilters() {
        List<CustomerInteraction> interactions = new ArrayList<>();
        interactions.add(testInteraction);
        Page<CustomerInteraction> page = new PageImpl<>(interactions, PageRequest.of(0, 10), 1);

        when(repository.searchInteractions(
            isNull(), isNull(), isNull(), isNull(), any(Pageable.class)
        )).thenReturn(page);

        var result = service.search(null, null, null, null, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }
}
