package com.qualityinspection.swequalityinspection.repository;

import com.qualityinspection.swequalityinspection.model.entities.ChecklistResultEntity;
import com.qualityinspection.swequalityinspection.model.entities.DefectReportEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DefectReportRepository extends JpaRepository<DefectReportEntity, Integer> {

    @Query(value = """
    SELECT p.product_type AS product, COUNT(*) AS defects
    FROM defect_reports d
    JOIN checklist_result c ON d.checklist_result_id = c.checklist_result_id
    JOIN batch b ON c.batch_id = b.batch_id
    JOIN products p ON b.product_id = p.product_id
    GROUP BY p.product_type
    ORDER BY defects DESC
    """, nativeQuery = true)
    List<Object[]> findDefectsGroupedByProduct();

    @Query(value = """
    SELECT 
        TO_CHAR(created_at, 'Mon') AS month,
        EXTRACT(MONTH FROM created_at) AS month_number,
        COUNT(*) AS defects
    FROM 
        defect_reports
    GROUP BY 
        month, month_number
    ORDER BY 
        month_number
    """, nativeQuery = true)
    List<Object[]> findDefectsOverTime();

    Page<DefectReportEntity> findByChecklistResult(ChecklistResultEntity checklistResult, Pageable pageable);

    Boolean existsByChecklistResult(ChecklistResultEntity checklistResult);

    int countByUserId(String userId);

    @Query(value = "SELECT COUNT(*) FROM defect_reports WHERE DATE(created_at) = CURRENT_DATE", nativeQuery = true)
    long countToday();


}
