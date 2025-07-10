package com.qualityinspection.swequalityinspection.model.responseDto;

public interface ChecklistAnswerResponseDto {
    Long getAnswerId();
    Long getResultId();
    Long getBatchId();
    String getProductName();
    String getItemDescription();
    Boolean getValue();
    String getComment();
    String getMediaUrl();
}
