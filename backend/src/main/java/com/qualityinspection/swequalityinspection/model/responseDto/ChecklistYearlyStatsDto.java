package com.qualityinspection.swequalityinspection.model.responseDto;


import java.util.List;

public record ChecklistYearlyStatsDto(
        int year,
        List<ChecklistMonthlyStatDto> months
) {}