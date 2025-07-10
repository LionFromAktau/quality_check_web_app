package com.qualityinspection.swequalityinspection.repository;

import com.qualityinspection.swequalityinspection.model.entities.BatchEntity;
import com.qualityinspection.swequalityinspection.model.entities.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {
    Page<ProductEntity> findAll(Specification<ProductEntity> spec, Pageable pageable);
}