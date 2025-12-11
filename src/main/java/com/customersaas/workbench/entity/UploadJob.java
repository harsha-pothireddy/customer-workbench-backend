package com.customersaas.workbench.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "upload_jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;

    private String status;

    private Integer totalRecords;

    private Integer successfulRecords;

    private Integer failedRecords;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    private LocalDateTime uploadedAt;

    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }
}
