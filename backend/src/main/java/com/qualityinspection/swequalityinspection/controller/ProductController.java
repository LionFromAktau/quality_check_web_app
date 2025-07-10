package com.qualityinspection.swequalityinspection.controller;

import com.qualityinspection.swequalityinspection.model.requestDto.ProductCreateUpdateDto;
import com.qualityinspection.swequalityinspection.model.responseDto.ProductResponseDto;
import com.qualityinspection.swequalityinspection.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    @Autowired
    ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("")
    public ResponseEntity<Page<ProductResponseDto>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort,
            @RequestParam(required = false) String name
    ) {
        return ResponseEntity.ok(productService.getProducts(page, size, sort, name));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getById(@PathVariable int id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ProductResponseDto> create(@RequestBody ProductCreateUpdateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> update(@PathVariable int id, @RequestBody ProductCreateUpdateDto dto) {
        return ResponseEntity.ok(productService.update(id, dto));
    }
}
