package com.qualityinspection.swequalityinspection.model.responseDto;

import com.qualityinspection.swequalityinspection.model.entities.ChecklistResultEntity;
import com.qualityinspection.swequalityinspection.model.enums.CheckResultStatus;

import java.time.LocalDateTime;

public class ChecklistFailedResultResponseDto {
    private Long resultId;
    private Integer batchId;
    private String productName;
    private CheckResultStatus status;
    private LocalDateTime createdAt;

    public ChecklistFailedResultResponseDto(Long resultId, Integer batchId, String productName, CheckResultStatus status, LocalDateTime createdAt) {
        this.resultId = resultId;
        this.batchId = batchId;
        this.productName = productName;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static ChecklistFailedResultResponseDto fromEntity(ChecklistResultEntity entity) {
        return new ChecklistFailedResultResponseDto(
                entity.getChecklistResultId(),
                entity.getBatch().getBatchId(),
                entity.getBatch().getProduct().getName(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }

    public Long getResultId() {
        return resultId;
    }

    public Integer getBatchId() {
        return batchId;
    }

    public String getProductName() {
        return productName;
    }

    public CheckResultStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
