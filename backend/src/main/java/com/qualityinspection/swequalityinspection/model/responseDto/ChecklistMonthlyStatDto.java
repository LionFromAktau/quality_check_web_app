package com.qualityinspection.swequalityinspection.model.responseDto;

public record ChecklistMonthlyStatDto(
        String month,
        int succeed,
        int failed,
        int total
) {}

