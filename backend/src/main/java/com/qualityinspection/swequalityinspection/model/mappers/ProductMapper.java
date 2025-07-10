package com.qualityinspection.swequalityinspection.model.mappers;

import com.qualityinspection.swequalityinspection.model.entities.ProductEntity;
import com.qualityinspection.swequalityinspection.model.requestDto.ProductCreateUpdateDto;
import com.qualityinspection.swequalityinspection.model.responseDto.ProductResponseDto;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = ChecklistItemMapper.class)
public interface ProductMapper {

    @Mapping(source = "checklistItemEntities", target = "checklistItems")
    ProductResponseDto toDto(ProductEntity entity);

    List<ProductResponseDto> toDtoList(List<ProductEntity> entities);

    ProductEntity toEntity(ProductCreateUpdateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget ProductEntity entity, ProductCreateUpdateDto dto);
}
