package com.qualityinspection.swequalityinspection.model.responseDto;

import java.time.LocalDateTime;
import java.util.List;

public record ProductResponseDto(
        Long id,
        String name,
        String productType,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<ChecklistItemResponseDto> checklistItems
) {}