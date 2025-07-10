package com.qualityinspection.swequalityinspection.model.mappers;

import com.qualityinspection.swequalityinspection.model.entities.BatchEntity;
import com.qualityinspection.swequalityinspection.model.entities.ProductEntity;
import com.qualityinspection.swequalityinspection.model.enums.BatchStatus;
import com.qualityinspection.swequalityinspection.model.requestDto.BatchCreateRequestDto;
import com.qualityinspection.swequalityinspection.model.requestDto.BatchUpdateRequestDto;
import com.qualityinspection.swequalityinspection.model.responseDto.BatchResponseDto;

import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BatchMapper {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "batchId", ignore = true)
    BatchEntity toEntity(BatchCreateRequestDto dto, @Context ProductEntity product);

    @AfterMapping
    default void setDefaultStatus(@MappingTarget BatchEntity entity,  @Context ProductEntity product) {
        entity.setProduct(product);
        entity.setStatus(BatchStatus.CHECKING);
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(BatchUpdateRequestDto dto, @MappingTarget BatchEntity entity);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    BatchResponseDto toDto(BatchEntity entity);

    List<BatchResponseDto> toDtoList(List<BatchEntity> entities);
}

