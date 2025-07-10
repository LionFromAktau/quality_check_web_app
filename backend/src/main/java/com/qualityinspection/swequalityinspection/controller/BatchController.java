package com.qualityinspection.swequalityinspection.controller;
import com.qualityinspection.swequalityinspection.model.requestDto.BatchCreateRequestDto;
import com.qualityinspection.swequalityinspection.model.requestDto.BatchUpdateRequestDto;
import com.qualityinspection.swequalityinspection.model.responseDto.BatchResponseDto;
import com.qualityinspection.swequalityinspection.service.BatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/batches")
public class BatchController {
    private final BatchService batchService;

    @Autowired
    public BatchController(BatchService batchService) {
        this.batchService = batchService;
    }

    @GetMapping("")
    public ResponseEntity<Page<BatchResponseDto>> getBatches(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort,
            @RequestParam(required = false) Integer productId
    ) {
        return ResponseEntity.ok(batchService.getBatches(page,size,sort,productId));
    }

    @GetMapping("all")
    public  ResponseEntity<List<BatchResponseDto>> getAllBatches(
            @RequestParam(defaultValue = "createdAt,desc") String[] sort,
            @RequestParam(required = false) Integer productId
    ) {
        return ResponseEntity.ok(batchService.getAllBatches(sort, productId));
    }


    @GetMapping("/{id}")
    public ResponseEntity<BatchResponseDto> get(@PathVariable Integer id) {
        return ResponseEntity.ok(batchService.get(id));
    }

    @PostMapping("")
    public ResponseEntity<BatchResponseDto> create(@RequestBody BatchCreateRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(batchService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BatchResponseDto> update(@PathVariable Integer id, @RequestBody BatchUpdateRequestDto dto) {
        return ResponseEntity.ok(batchService.update(id, dto));
    }

}