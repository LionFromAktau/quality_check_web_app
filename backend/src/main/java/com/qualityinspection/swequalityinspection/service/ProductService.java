package com.qualityinspection.swequalityinspection.service;

import com.qualityinspection.swequalityinspection.model.entities.ProductEntity;
import com.qualityinspection.swequalityinspection.model.mappers.ProductMapper;
import com.qualityinspection.swequalityinspection.model.requestDto.ProductCreateUpdateDto;
import com.qualityinspection.swequalityinspection.model.responseDto.ProductResponseDto;
import com.qualityinspection.swequalityinspection.repository.ProductRepository;
import com.qualityinspection.swequalityinspection.repository.specification.ProductSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public Page<ProductResponseDto> getProducts(int page, int size, String[] sort, String name) {
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Sort sorting = Sort.by(new Sort.Order(direction, sort[0]));

        Pageable pageable = PageRequest.of(page, size, sorting);
        Specification<ProductEntity> spec = ProductSpecification.nameContains(name);

        return productRepository.findAll(spec, pageable)
                .map(productMapper::toDto);
    }

    public ProductResponseDto getById(int id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        return productMapper.toDto(product);
    }

    public ProductResponseDto create(ProductCreateUpdateDto dto) {
        ProductEntity product = productMapper.toEntity(dto);
        ProductEntity saved = productRepository.save(product);
        return productMapper.toDto(saved);
    }

    public ProductResponseDto update(int id, ProductCreateUpdateDto dto) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        productMapper.updateEntity(product, dto);
        return productMapper.toDto(productRepository.save(product));
    }


}
