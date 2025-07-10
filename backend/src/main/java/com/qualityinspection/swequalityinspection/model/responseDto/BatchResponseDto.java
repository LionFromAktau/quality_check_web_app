package com.qualityinspection.swequalityinspection.model.responseDto;

import com.qualityinspection.swequalityinspection.model.enums.BatchStatus;

import java.time.LocalDateTime;

public record BatchResponseDto(
        Integer batchId,
        Long productId,
        String productName,
        Integer quantity,
        BatchStatus status,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}