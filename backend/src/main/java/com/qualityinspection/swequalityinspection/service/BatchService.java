package com.qualityinspection.swequalityinspection.service;

import com.qualityinspection.swequalityinspection.exceptions.ExceptionMessages;
import com.qualityinspection.swequalityinspection.model.entities.BatchEntity;
import com.qualityinspection.swequalityinspection.model.entities.ProductEntity;
import com.qualityinspection.swequalityinspection.model.mappers.BatchMapper;
import com.qualityinspection.swequalityinspection.model.requestDto.BatchCreateRequestDto;
import com.qualityinspection.swequalityinspection.model.requestDto.BatchUpdateRequestDto;
import com.qualityinspection.swequalityinspection.model.responseDto.BatchResponseDto;
import com.qualityinspection.swequalityinspection.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;


@Service
public class BatchService {
    private final BatchRepository batchRepository;
    private final ProductRepository productRepo;
    private final BatchMapper mapper;

    public BatchService(BatchRepository batchRepository, ProductRepository productRepo, BatchMapper batchMapper) {
        this.batchRepository = batchRepository;
        this.productRepo = productRepo;
        this.mapper = batchMapper;
    }

    public Page<BatchResponseDto> getBatches(int page, int size, String[] sort, Integer productId) {
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<BatchEntity> batchPage;
        if (productId != null) {
            ProductEntity product = productRepo.findById(productId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ExceptionMessages.ProductNotFound));
            batchPage = batchRepository.findByProduct(product, pageable);
        }else{
            batchPage = batchRepository.findAll(pageable);
        }
        return batchPage.map(mapper::toDto);
    }

    public List<BatchResponseDto> getAllBatches(String[] sort, Integer productId) {
        // Split sort values and build Sort object
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Sort sorting = Sort.by(new Sort.Order(direction, sort[0]));
        // Fetch sorted data
        List<BatchEntity> entities;
        if (productId != null) {
            ProductEntity product = productRepo.findById(productId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ExceptionMessages.ProductNotFound));
            entities = batchRepository.findAllByProduct(product, sorting);
        }else{
            entities = batchRepository.findAll(sorting);
        }

        // Map to DTOs
        return mapper.toDtoList(entities);
    }


    public BatchResponseDto get(int batchId) {
        BatchEntity entity = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ExceptionMessages.BatchNotFound));
        return mapper.toDto(entity);
    }

    public BatchResponseDto create(BatchCreateRequestDto dto) {
        ProductEntity product = productRepo.findById(dto.productId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ExceptionMessages.ProductNotFound));
        BatchEntity entity = mapper.toEntity(dto, product);
        BatchEntity saved = batchRepository.save(entity);
        return mapper.toDto(saved);
    }



    public BatchResponseDto update(Integer id, BatchUpdateRequestDto dto) {
        BatchEntity entity = batchRepository.findById(id)
                .orElseThrow(() ->  new ResponseStatusException(HttpStatus.NOT_FOUND, ExceptionMessages.BatchNotFound));

        mapper.updateEntity(dto, entity);
        return mapper.toDto(batchRepository.save(entity));
    }

}
