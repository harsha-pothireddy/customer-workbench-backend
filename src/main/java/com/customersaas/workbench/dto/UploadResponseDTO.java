package com.customersaas.workbench.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadResponseDTO {

    private boolean success;

    private String message;

    private Long uploadJobId;

    private Integer processedRecords;

    private Integer failedRecords;
}
