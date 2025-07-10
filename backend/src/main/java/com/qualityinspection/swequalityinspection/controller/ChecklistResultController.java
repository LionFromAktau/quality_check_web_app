package com.qualityinspection.swequalityinspection.controller;

import com.qualityinspection.swequalityinspection.model.entities.ChecklistResultEntity;
import com.qualityinspection.swequalityinspection.model.requestDto.ChecklistResultRequest;
import com.qualityinspection.swequalityinspection.model.responseDto.ChecklistAnswerResponseDto;
import com.qualityinspection.swequalityinspection.model.responseDto.ChecklistFailedResultResponseDto;
import com.qualityinspection.swequalityinspection.model.responseDto.ChecklistYearlyStatsDto;
import com.qualityinspection.swequalityinspection.service.ChecklistResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checklist/results")
public class ChecklistResultController {
    private final ChecklistResultService checklistResultService;

    @Autowired
    public ChecklistResultController(ChecklistResultService checklistResultService) {
        this.checklistResultService = checklistResultService;
    }

    @PostMapping("")
    public ResponseEntity<List<ChecklistAnswerResponseDto>> createChecklistResult(@ModelAttribute  ChecklistResultRequest checklistResultRequest) {
        return ResponseEntity.ok(checklistResultService.createChecklistResult(checklistResultRequest));
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ChecklistYearlyStatsDto>> getChecklistStats() {
        return ResponseEntity.ok(checklistResultService.getChecklistStats());
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<ChecklistAnswerResponseDto>> getChecklistAnswer(@PathVariable("id") Long resultId) {
        return ResponseEntity.ok(checklistResultService.getChecklistAnswers(resultId));
    }

    @GetMapping("/failed")
    public ResponseEntity<List<ChecklistFailedResultResponseDto>> getChecklistFailed() {
        return ResponseEntity.ok(checklistResultService.getChecklistFailedResults());
    }

///api/checklist/results/failed
//    [
//    {
//        "resultId": 0,
//            "batchId": 0,
//            "productName": "string",
//            "status": "SUCCESS",
//            "createdAt": "2025-07-04T11:12:19.410Z"
//    }
//]
}
