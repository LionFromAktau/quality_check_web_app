package com.qualityinspection.swequalityinspection.service;

import com.qualityinspection.swequalityinspection.exceptions.ExceptionMessages;
import com.qualityinspection.swequalityinspection.model.entities.*;
import com.qualityinspection.swequalityinspection.model.mappers.DefectReportMapper;
import com.qualityinspection.swequalityinspection.model.requestDto.DefectReportRequest;
import com.qualityinspection.swequalityinspection.model.responseDto.DefectReportResponseDto;
import com.qualityinspection.swequalityinspection.repository.ChecklistResultRepository;
import com.qualityinspection.swequalityinspection.repository.DefectReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DefectReportService {
    private final DefectReportRepository reportRepository;
    private final ChecklistResultRepository checklistResultRepository;
    private final DefectReportMapper mapper;

    @Autowired
    DefectReportService(DefectReportRepository reportRepository,
                        ChecklistResultRepository checklistResultRepository,
                        @Qualifier("defectReportMapperImpl") DefectReportMapper mapper) {
        this.reportRepository = reportRepository;
        this.checklistResultRepository = checklistResultRepository;
        this.mapper = mapper;
    }

    public DefectReportResponseDto createDefectReport(DefectReportRequest defectReportRequest) {
        ChecklistResultEntity resultEntity = checklistResultRepository.findById(defectReportRequest.checkResultId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ExceptionMessages.ChecklistResultNotFound));
        if (reportRepository.existsByChecklistResult(resultEntity)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ExceptionMessages.ResultAlreadyReported);
        }
        String userId = defectReportRequest.userId();

        DefectReportEntity entity = new DefectReportEntity(
                resultEntity,
                userId,
                defectReportRequest.description(),
                defectReportRequest.status()
        );
        return mapper.toDto(reportRepository.save(entity));
    }

    public List<Map<String, Object>> getDefectsByProductType() {
        List<Object[]> raw = reportRepository.findDefectsGroupedByProduct();

        return raw.stream()
                .map(row -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("productType", row[0]);
                    map.put("defects_count", ((Number) row[1]).intValue());
                    return map;
                })
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getDefectsOverTime() {
        List<Object[]> rows = reportRepository.findDefectsOverTime();

        return rows.stream()
                .map(row -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("month", row[0]);
                    data.put("defects", ((Number) row[2]).intValue());
                    return data;
                })
                .collect(Collectors.toList());
    }

    public Page<DefectReportResponseDto> getDefectReport(int page, int size, String[] sort, Integer resultId) {
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<DefectReportEntity> defectPage;
        if (resultId != null) {
            ChecklistResultEntity resultEntity = checklistResultRepository.findById(resultId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ExceptionMessages.ChecklistResultNotFound));
            defectPage = reportRepository.findByChecklistResult(resultEntity, pageable);
        } else {
            defectPage = reportRepository.findAll(pageable);
        }
        return defectPage.map(mapper::toDto);
    }

}
