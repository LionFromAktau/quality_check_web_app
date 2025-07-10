package com.qualityinspection.swequalityinspection.model.responseDto;

public record UserResponseDto(
        String userId,
        String username,
        String email,
        String firstName,
        String lastName,
        String role,
        Integer checklistFilled,
        Integer defectReportCreated
) {}

