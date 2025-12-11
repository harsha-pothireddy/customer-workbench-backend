package com.customersaas.workbench.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchResultDTO {

    private List<CustomerInteractionDTO> interactions;

    private long totalElements;

    private int totalPages;

    private int currentPage;

    private int pageSize;

    public static SearchResultDTO fromPage(Page<CustomerInteractionDTO> page) {
        return SearchResultDTO.builder()
            .interactions(page.getContent())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .currentPage(page.getNumber())
            .pageSize(page.getSize())
            .build();
    }
}
