package com.qualityinspection.swequalityinspection.controller;

import com.qualityinspection.swequalityinspection.model.requestDto.DefectReportRequest;
import com.qualityinspection.swequalityinspection.model.responseDto.DefectReportResponseDto;
import com.qualityinspection.swequalityinspection.service.DefectReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/defect/report")
public class DefectReportController {
    private final DefectReportService defectReportService;
    @Autowired
    DefectReportController(DefectReportService defectReportService) {
        this.defectReportService = defectReportService;
    }

    @GetMapping("/by-product")
    public List<Map<String, Object>> getDefectReportByProduct() {
        return defectReportService.getDefectsByProductType();
    }

    @GetMapping("/over-time")
    public List<Map<String, Object>> getDefectReportOverTime() {
        return defectReportService.getDefectsOverTime();
    }

    @PostMapping("")
    public ResponseEntity<DefectReportResponseDto> createDefectReport(@RequestBody DefectReportRequest defectReportRequest) {
        return ResponseEntity.ok(defectReportService.createDefectReport(defectReportRequest));
    }

    @GetMapping("")
    public Page<DefectReportResponseDto> getDefectReport(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort,
            @RequestParam(required = false) Integer checklistResultId
    ){
        return defectReportService.getDefectReport(page, size, sort, checklistResultId);
    }


}
