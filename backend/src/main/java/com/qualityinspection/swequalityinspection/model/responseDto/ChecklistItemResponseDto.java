package com.qualityinspection.swequalityinspection.model.responseDto;

public record ChecklistItemResponseDto(
        Long id,
        String description,
        Boolean isMandatory
) {
}
