package com.qualityinspection.swequalityinspection.service;

import com.qualityinspection.swequalityinspection.repository.ChecklistResultRepository;
import com.qualityinspection.swequalityinspection.repository.DefectReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class KpiService {

    @Autowired
    private ChecklistResultRepository checklistResultRepository;

    @Autowired
    private DefectReportRepository defectReportRepository;

    public Map<String, Long> getKpiStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("productsChecked", checklistResultRepository.count());
        stats.put("checksToday", checklistResultRepository.countToday()); // Реализуй custom query
        stats.put("defectsDetected", defectReportRepository.count());
        stats.put("defectsToday", defectReportRepository.countToday()); // Тоже custom query
        return stats;
    }
}
