package com.qualityinspection.swequalityinspection.model.requestDto;

import com.qualityinspection.swequalityinspection.model.enums.BatchStatus;

public record BatchCreateRequestDto(
        Integer productId,
        Integer quantity,
        String notes
) {}
