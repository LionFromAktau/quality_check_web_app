package com.qualityinspection.swequalityinspection.repository;

import com.qualityinspection.swequalityinspection.model.entities.ChecklistAnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChecklistAnswerRepository extends JpaRepository<ChecklistAnswerEntity, Integer> {
}
