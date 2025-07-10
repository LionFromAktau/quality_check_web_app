package com.qualityinspection.swequalityinspection.service;

import com.qualityinspection.swequalityinspection.model.entities.ProductEntity;
import com.qualityinspection.swequalityinspection.model.mappers.ProductMapper;
import com.qualityinspection.swequalityinspection.model.requestDto.ProductCreateUpdateDto;
import com.qualityinspection.swequalityinspection.model.responseDto.ProductResponseDto;
import com.qualityinspection.swequalityinspection.repository.ProductRepository;
import com.qualityinspection.swequalityinspection.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    ProductMapper productMapper;

    @InjectMocks
    ProductService productService;

    ProductEntity productEntity;
    ProductResponseDto productResponseDto;
    ProductCreateUpdateDto productCreateUpdateDto;

    @BeforeEach
    void setup() {
        productEntity = new ProductEntity();
        productEntity.setId(1L);
        productEntity.setName("TestProduct");
        productEntity.setProductType("Electronics");
        productEntity.setCreatedAt(LocalDateTime.now());
        productEntity.setUpdatedAt(LocalDateTime.now());

        productResponseDto = new ProductResponseDto(
                1L,
                "TestProduct",
                "Electronics",
                LocalDateTime.now(),
                LocalDateTime.now(),
                List.of() // empty checklist for simplicity
        );

        productCreateUpdateDto = new ProductCreateUpdateDto("TestProduct", "Electronics");
    }

    @Test
    void getById_ReturnsProductDto() {
        when(productRepository.findById(1)).thenReturn(Optional.of(productEntity));
        when(productMapper.toDto(productEntity)).thenReturn(productResponseDto);

        ProductResponseDto result = productService.getById(1);

        assertNotNull(result);
        assertEquals("Electronics", result.productType());  // <-- use productType(), not type()
        assertEquals("TestProduct", result.name());
        verify(productRepository).findById(1);
    }

    @Test
    void create_SavesAndReturnsDto() {
        when(productMapper.toEntity(productCreateUpdateDto)).thenReturn(productEntity);
        when(productRepository.save(productEntity)).thenReturn(productEntity);
        when(productMapper.toDto(productEntity)).thenReturn(productResponseDto);

        ProductResponseDto result = productService.create(productCreateUpdateDto);

        assertEquals("Electronics", result.productType());
        verify(productRepository).save(productEntity);
    }

    @Test
    void update_ExistingProduct_UpdatesAndReturnsDto() {
        when(productRepository.findById(1)).thenReturn(Optional.of(productEntity));
        doAnswer(invocation -> {
            ProductEntity entity = invocation.getArgument(0);
            ProductCreateUpdateDto dto = invocation.getArgument(1);
            entity.setName(dto.name());
            entity.setProductType(dto.productType());
            return null;
        }).when(productMapper).updateEntity(productEntity, productCreateUpdateDto);
        when(productRepository.save(productEntity)).thenReturn(productEntity);
        when(productMapper.toDto(productEntity)).thenReturn(productResponseDto);

        ProductResponseDto result = productService.update(1, productCreateUpdateDto);

        assertEquals("Electronics", result.productType());
        assertEquals("TestProduct", result.name());
        verify(productRepository).save(productEntity);
    }
}
