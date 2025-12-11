package com.customersaas.workbench.controller;

import com.customersaas.workbench.dto.UploadResponseDTO;
import com.customersaas.workbench.entity.UploadJob;
import com.customersaas.workbench.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class UploadController {

    private final FileUploadService fileUploadService;

    @PostMapping
    public ResponseEntity<UploadResponseDTO> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(
                    UploadResponseDTO.builder()
                        .success(false)
                        .message("File is empty")
                        .build()
                );
            }

            UploadJob uploadJob = fileUploadService.processUpload(file);

            return ResponseEntity.ok(
                UploadResponseDTO.builder()
                    .success("COMPLETED".equals(uploadJob.getStatus()))
                    .message(uploadJob.getStatus().equals("COMPLETED") 
                        ? "File uploaded successfully" 
                        : uploadJob.getErrorMessage())
                    .uploadJobId(uploadJob.getId())
                    .processedRecords(uploadJob.getSuccessfulRecords())
                    .failedRecords(uploadJob.getFailedRecords())
                    .build()
            );

        } catch (IOException e) {
            log.error("Error uploading file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                UploadResponseDTO.builder()
                    .success(false)
                    .message("Error processing file: " + e.getMessage())
                    .build()
            );
        }
    }
}
