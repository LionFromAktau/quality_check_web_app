package com.qualityinspection.swequalityinspection.model.requestDto;

import com.qualityinspection.swequalityinspection.model.enums.BatchStatus;

public record BatchUpdateRequestDto(
        Integer quantity,
        BatchStatus status,
        String notes
) {}