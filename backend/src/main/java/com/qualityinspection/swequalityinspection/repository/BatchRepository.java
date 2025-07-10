package com.qualityinspection.swequalityinspection.repository;

import com.qualityinspection.swequalityinspection.model.entities.BatchEntity;
import com.qualityinspection.swequalityinspection.model.entities.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BatchRepository extends JpaRepository<BatchEntity, Integer> {
    Page<BatchEntity> findByProduct(ProductEntity product, Pageable pageable);

    List<BatchEntity> findAllByProduct(ProductEntity product, Sort sort);
}

