package com.customersaas.workbench.service;

import com.customersaas.workbench.entity.CustomerInteraction;
import com.customersaas.workbench.entity.UploadJob;
import com.customersaas.workbench.repository.UploadJobRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadService {

    private final CustomerInteractionService interactionService;
    private final UploadJobRepository uploadJobRepository;
    private final ObjectMapper objectMapper;

    public UploadJob processUpload(MultipartFile file) throws IOException {
        UploadJob uploadJob = UploadJob.builder()
            .filename(file.getOriginalFilename())
            .status("PROCESSING")
            .totalRecords(0)
            .successfulRecords(0)
            .failedRecords(0)
            .build();

        uploadJob = uploadJobRepository.save(uploadJob);

        try {
            String filename = file.getOriginalFilename();
            List<CustomerInteraction> interactions = new ArrayList<>();

            if (filename != null && filename.endsWith(".csv")) {
                interactions = parseCsv(file);
            } else if (filename != null && filename.endsWith(".json")) {
                interactions = parseJson(file);
            } else {
                throw new IllegalArgumentException("Unsupported file format. Only CSV and JSON are supported.");
            }

            // Save all interactions
            int successful = 0;
            for (CustomerInteraction interaction : interactions) {
                try {
                    interactionService.save(interaction);
                    successful++;
                } catch (Exception e) {
                    log.error("Error saving interaction: {}", e.getMessage());
                    uploadJob.setFailedRecords(uploadJob.getFailedRecords() + 1);
                }
            }

            uploadJob.setTotalRecords(interactions.size());
            uploadJob.setSuccessfulRecords(successful);
            uploadJob.setStatus("COMPLETED");
            uploadJob.setCompletedAt(LocalDateTime.now());

        } catch (Exception e) {
            log.error("Error processing upload: {}", e.getMessage(), e);
            uploadJob.setStatus("FAILED");
            uploadJob.setErrorMessage(e.getMessage());
            uploadJob.setCompletedAt(LocalDateTime.now());
        }

        return uploadJobRepository.save(uploadJob);
    }

    private List<CustomerInteraction> parseCsv(MultipartFile file) throws IOException {
        List<CustomerInteraction> interactions = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

            for (CSVRecord record : csvParser) {
                try {
                    String timestamp = record.get("timestamp");
                    LocalDateTime parsedTimestamp = LocalDateTime.parse(timestamp, formatter);

                    CustomerInteraction interaction = CustomerInteraction.builder()
                        .productId(record.get("product_id"))
                        .customerId(record.get("customer_id"))
                        .customerRating(parseInteger(record.get("customer_rating")))
                        .feedback(record.get("feedback"))
                        .timestamp(parsedTimestamp)
                        .responsesFromCustomerSupport(record.get("responses_from_customer_support"))
                        .interactionType(inferInteractionType(record.get("feedback")))
                        .build();

                    interactions.add(interaction);
                } catch (Exception e) {
                    log.warn("Error parsing CSV record: {}", e.getMessage());
                }
            }
        }

        return interactions;
    }

    private List<CustomerInteraction> parseJson(MultipartFile file) throws IOException {
        List<CustomerInteraction> interactions = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            StringBuilder jsonContent = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }

            String content = jsonContent.toString().trim();
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

            if (content.startsWith("[")) {
                List<Map<String, Object>> records = objectMapper.readValue(content,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class));

                for (Map<String, Object> record : records) {
                    try {
                        String timestamp = record.get("timestamp").toString();
                        LocalDateTime parsedTimestamp = LocalDateTime.parse(timestamp, formatter);

                        CustomerInteraction interaction = CustomerInteraction.builder()
                            .productId(record.get("product_id").toString())
                            .customerId(record.get("customer_id").toString())
                            .customerRating(parseInteger(record.get("customer_rating")))
                            .feedback(record.get("feedback").toString())
                            .timestamp(parsedTimestamp)
                            .responsesFromCustomerSupport(record.get("responses_from_customer_support").toString())
                            .interactionType(inferInteractionType(record.get("feedback").toString()))
                            .build();

                        interactions.add(interaction);
                    } catch (Exception e) {
                        log.warn("Error parsing JSON record: {}", e.getMessage());
                    }
                }
            }
        }

        return interactions;
    }

    private Integer parseInteger(Object value) {
        if (value == null) return null;
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String inferInteractionType(String feedback) {
        if (feedback == null) return "unknown";
        String lower = feedback.toLowerCase();
        if (lower.contains("email")) return "email";
        if (lower.contains("chat")) return "chat";
        if (lower.contains("ticket") || lower.contains("support")) return "ticket";
        return "feedback";
    }
}
