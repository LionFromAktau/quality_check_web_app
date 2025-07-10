package com.qualityinspection.swequalityinspection.model.responseDto;

import java.time.LocalDateTime;

public record DefectReportResponseDto(
        long defectId,
        long checklistResultId,
        String userId,
        String description,
        String status,
        String media,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
