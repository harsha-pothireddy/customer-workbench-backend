package com.customersaas.workbench;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.http.MediaType;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public class UploadIntegrationTest {

    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    void uploadCsvAndSearch_shouldIngestAndRetrieveData() throws Exception {
        String csv = "product_id,customer_id,customer_rating,feedback,timestamp,responses_from_customer_support\n"
            + "PROD-1,CUST-INT-1,5,Test email feedback,2025-12-11T16:04:50,Thank you for your feedback!\n"
            + "PROD-1,CUST-INT-1,4,Another feedback,2025-12-11T17:00:00,Response here\n";

        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", csv.getBytes());

        // Upload
        mockMvc.perform(multipart("/api/uploads").file(file))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.processedRecords").value(2));

        // Search
        mockMvc.perform(get("/api/interactions/search").param("customerId", "CUST-INT-1").param("page", "0").param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalElements").value(2))
            .andExpect(jsonPath("$.interactions[0].customerId").value("CUST-INT-1"));
    }
}
