package com.customersaas.workbench;

import com.customersaas.workbench.entity.CustomerInteraction;
import com.customersaas.workbench.repository.CustomerInteractionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerInteractionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerInteractionRepository repository;

    @Test
    void testUploadAndRetrieveInteraction() throws Exception {
        // Arrange
        CustomerInteraction interaction = CustomerInteraction.builder()
            .productId("PROD-001")
            .customerId("CUST-001")
            .customerRating(5)
            .feedback("Excellent support!")
            .timestamp(LocalDateTime.now())
            .responsesFromCustomerSupport("Thank you for your feedback!")
            .interactionType("email")
            .build();

        repository.save(interaction);

        // Act & Assert - Verify data was saved
        var result = repository.searchInteractions("CUST-001", null, null, null, PageRequest.of(0, 10));
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getCustomerId()).isEqualTo("CUST-001");
    }

    @Test
    void testSearchByCustomerId() throws Exception {
        // Arrange
        CustomerInteraction interaction1 = CustomerInteraction.builder()
            .productId("PROD-001")
            .customerId("CUST-001")
            .customerRating(5)
            .feedback("Great!")
            .timestamp(LocalDateTime.now())
            .responsesFromCustomerSupport("Thank you!")
            .interactionType("email")
            .build();

        CustomerInteraction interaction2 = CustomerInteraction.builder()
            .productId("PROD-001")
            .customerId("CUST-002")
            .customerRating(4)
            .feedback("Good!")
            .timestamp(LocalDateTime.now())
            .responsesFromCustomerSupport("Appreciated!")
            .interactionType("chat")
            .build();

        repository.save(interaction1);
        repository.save(interaction2);

        // Act & Assert
        mockMvc.perform(get("/api/interactions/search")
                .param("customerId", "CUST-001")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.interactions[0].customerId").value("CUST-001"));
    }

    @Test
    void testSearchByInteractionType() throws Exception {
        // Arrange
        CustomerInteraction chatInteraction = CustomerInteraction.builder()
            .productId("PROD-001")
            .customerId("CUST-001")
            .customerRating(5)
            .feedback("chat support")
            .timestamp(LocalDateTime.now())
            .responsesFromCustomerSupport("Support response")
            .interactionType("chat")
            .build();

        repository.save(chatInteraction);

        // Act & Assert
        mockMvc.perform(get("/api/interactions/search")
                .param("interactionType", "chat")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.interactions[0].interactionType").value("chat"));
    }

    @Test
    void testSearchWithDateRange() throws Exception {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1);
        LocalDateTime tomorrow = now.plusDays(1);

        CustomerInteraction interaction = CustomerInteraction.builder()
            .productId("PROD-001")
            .customerId("CUST-001")
            .customerRating(5)
            .feedback("Test feedback")
            .timestamp(now)
            .responsesFromCustomerSupport("Response")
            .interactionType("email")
            .build();

        repository.save(interaction);

        // Act & Assert
        mockMvc.perform(get("/api/interactions/search")
                .param("startDate", yesterday.toString())
                .param("endDate", tomorrow.toString())
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.interactions").isArray());
    }
}
