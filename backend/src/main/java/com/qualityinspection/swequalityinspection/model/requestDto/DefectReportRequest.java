package com.qualityinspection.swequalityinspection.model.requestDto;

import com.qualityinspection.swequalityinspection.model.enums.DefectStatus;

public record DefectReportRequest(
        String userId,
        Integer checkResultId,
        DefectStatus status,
        String description
) {}
