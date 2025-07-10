package com.qualityinspection.swequalityinspection.service;

import com.qualityinspection.swequalityinspection.exceptions.ExceptionMessages;
import com.qualityinspection.swequalityinspection.model.entities.BatchEntity;
import com.qualityinspection.swequalityinspection.model.entities.ProductEntity;
import com.qualityinspection.swequalityinspection.model.enums.BatchStatus;
import com.qualityinspection.swequalityinspection.model.mappers.BatchMapper;
import com.qualityinspection.swequalityinspection.model.requestDto.BatchCreateRequestDto;
import com.qualityinspection.swequalityinspection.model.requestDto.BatchUpdateRequestDto;
import com.qualityinspection.swequalityinspection.model.responseDto.BatchResponseDto;
import com.qualityinspection.swequalityinspection.repository.BatchRepository;
import com.qualityinspection.swequalityinspection.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BatchServiceTest {

    @Mock
    private BatchRepository batchRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BatchMapper batchMapper;

    @InjectMocks
    private BatchService batchService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_shouldReturnDto_whenProductExists() {
        BatchCreateRequestDto requestDto = new BatchCreateRequestDto(1, 50, "Test notes");
        ProductEntity product = new ProductEntity();
        BatchEntity entity = new BatchEntity();
        BatchEntity saved = new BatchEntity();
        BatchResponseDto responseDto = new BatchResponseDto(1, 1L, "Product", 50, BatchStatus.CHECKING, "Test notes", null, null);

        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(batchMapper.toEntity(requestDto, product)).thenReturn(entity);
        when(batchRepository.save(entity)).thenReturn(saved);
        when(batchMapper.toDto(saved)).thenReturn(responseDto);

        var result = batchService.create(requestDto);

        assertEquals(responseDto, result);
        verify(productRepository).findById(1);
        verify(batchRepository).save(entity);
    }

    @Test
    void create_shouldThrowException_whenProductNotFound() {
        BatchCreateRequestDto requestDto = new BatchCreateRequestDto(99, 10, "nope");

        when(productRepository.findById(99)).thenReturn(Optional.empty());

        var ex = assertThrows(ResponseStatusException.class, () -> batchService.create(requestDto));
        assertEquals(ExceptionMessages.ProductNotFound, ex.getReason());
    }

    @Test
    void get_shouldReturnDto_whenBatchExists() {
        BatchEntity entity = new BatchEntity();
        BatchResponseDto dto = new BatchResponseDto(1, 1L, "Test", 100, BatchStatus.CHECKING, null, null, null);

        when(batchRepository.findById(1)).thenReturn(Optional.of(entity));
        when(batchMapper.toDto(entity)).thenReturn(dto);

        var result = batchService.get(1);

        assertEquals(dto, result);
    }

    @Test
    void get_shouldThrow_whenBatchNotFound() {
        when(batchRepository.findById(42)).thenReturn(Optional.empty());

        var ex = assertThrows(ResponseStatusException.class, () -> batchService.get(42));
        assertEquals(ExceptionMessages.BatchNotFound, ex.getReason());
    }

    @Test
    void update_shouldUpdateAndReturnDto() {
        BatchEntity entity = new BatchEntity();
        BatchUpdateRequestDto dto = new BatchUpdateRequestDto(200, BatchStatus.CHECKED, "updated");
        BatchResponseDto expected = new BatchResponseDto(1, 1L, "Test", 200, BatchStatus.CHECKED, "updated", null, null);

        when(batchRepository.findById(1)).thenReturn(Optional.of(entity));
        // updateEntity is void
        doNothing().when(batchMapper).updateEntity(dto, entity);
        when(batchRepository.save(entity)).thenReturn(entity);
        when(batchMapper.toDto(entity)).thenReturn(expected);

        var result = batchService.update(1, dto);

        assertEquals(expected, result);
    }
}

