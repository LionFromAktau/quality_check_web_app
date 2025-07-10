package com.qualityinspection.swequalityinspection.model.mappers;

import com.qualityinspection.swequalityinspection.model.entities.DefectReportEntity;
import com.qualityinspection.swequalityinspection.model.responseDto.DefectReportResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DefectReportMapper {
    @Mapping(source = "checklistResultId.checklistResultId", target = "checklistResultId")
    DefectReportResponseDto toDto(DefectReportEntity entity);
}
