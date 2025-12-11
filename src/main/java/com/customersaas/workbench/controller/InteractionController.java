package com.customersaas.workbench.controller;

import com.customersaas.workbench.dto.SearchResultDTO;
import com.customersaas.workbench.service.CustomerInteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/interactions")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class InteractionController {

    private final CustomerInteractionService interactionService;

    @GetMapping("/search")
    public ResponseEntity<SearchResultDTO> search(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String interactionType,
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        SearchResultDTO result = interactionService.search(customerId, interactionType, startDate, endDate, pageable);

        return ResponseEntity.ok(result);
    }
}
