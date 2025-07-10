package com.qualityinspection.swequalityinspection.repository.specification;

import com.qualityinspection.swequalityinspection.model.entities.ProductEntity;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {

    public static Specification<ProductEntity> nameContains(String keyword) {
        return (root, query, builder) -> keyword == null || keyword.isBlank()
                ? builder.conjunction()
                : builder.like(builder.lower(root.get("name")), "%" + keyword.toLowerCase() + "%");
    }
}
