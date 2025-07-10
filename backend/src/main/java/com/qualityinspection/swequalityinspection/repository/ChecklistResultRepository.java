package com.qualityinspection.swequalityinspection.repository;

import com.qualityinspection.swequalityinspection.model.entities.ChecklistResultEntity;
import com.qualityinspection.swequalityinspection.model.responseDto.ChecklistAnswerResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChecklistResultRepository extends JpaRepository<ChecklistResultEntity, Integer> {

    @Query(value = """
            SELECT
        EXTRACT(YEAR FROM created_at) AS year,
        EXTRACT(MONTH FROM created_at) AS month_number,
        TO_CHAR(created_at, 'Mon') AS month,
        status,
        COUNT(*) AS count\s
    FROM checklist_result\s
    GROUP BY year, month_number, month, status\s
    ORDER BY year, month_number;
    
    """, nativeQuery = true)
    List<Object[]> findChecklistStatsGroupedByMonthAndStatus();

    @Query("""
        SELECT
            ca.checklistAnswerId as answerId,
            cr.checklistResultId as resultId,
            b.batchId as batchId,
            p.name as productName,
            ca.itemDescription as itemDescription,
            ca.value as value,
            ca.comment as comment,
            ca.mediaUrl as mediaUrl
        FROM ChecklistAnswerEntity ca
        JOIN ca.result cr
        JOIN cr.batch b
        JOIN b.product p
            WHERE cr.checklistResultId = :resultId
    """)
    List<ChecklistAnswerResponseDto> findDetailedAnswers(@Param("resultId") Long resultId);


    @Query("""
SELECT c
FROM ChecklistResultEntity c
WHERE NOT EXISTS (
    SELECT dr
    FROM DefectReportEntity dr
    WHERE dr.checklistResult = c
)
AND c.status = 'FAIL'
""")
    List<ChecklistResultEntity> findFailedWithoutDefectReport();

    int countByUserId(String userId);

    @Query("SELECT COUNT(c) FROM ChecklistResultEntity c")
    long countAll();

    @Query(value = "SELECT COUNT(*) FROM checklist_result WHERE DATE(created_at) = CURRENT_DATE", nativeQuery = true)
    long countToday();


//    // Новый способ — полностью загружает batch, product, checklistItems и user
//    @Query("""
//        SELECT cr FROM ChecklistResultEntity cr
//        JOIN FETCH cr.batch b
//        JOIN FETCH b.product p
//        LEFT JOIN FETCH p.checklistItemEntities
//        JOIN FETCH cr.user u
//        LEFT JOIN FETCH cr.answers a
//        LEFT JOIN FETCH a.item
//        WHERE cr.status = :status AND u.userId = :userId
//    """)
//    List<ChecklistResultEntity> findFailedResultsWithDetails(@Param("userId") Integer userId, @Param("status") CheckResultStatus status);
}
