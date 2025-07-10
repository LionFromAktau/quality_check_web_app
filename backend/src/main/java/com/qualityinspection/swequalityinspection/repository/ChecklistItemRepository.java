package com.qualityinspection.swequalityinspection.repository;

import com.qualityinspection.swequalityinspection.model.entities.ChecklistItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChecklistItemRepository extends JpaRepository<ChecklistItemEntity, Integer> {
}
