package com.qualityinspection.swequalityinspection.model.mappers;

import com.qualityinspection.swequalityinspection.model.entities.ChecklistItemEntity;
import com.qualityinspection.swequalityinspection.model.responseDto.ChecklistItemResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChecklistItemMapper {
    @Mapping(source = "itemId", target = "id")
    @Mapping(source = "mandatory", target = "isMandatory")
    ChecklistItemResponseDto toDto(ChecklistItemEntity entity);
    List<ChecklistItemResponseDto> toDtoList(List<ChecklistItemEntity> entities);
}
