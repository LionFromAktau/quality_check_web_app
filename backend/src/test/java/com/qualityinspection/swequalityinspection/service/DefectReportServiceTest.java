package com.qualityinspection.swequalityinspection.service;

import com.qualityinspection.swequalityinspection.exceptions.ExceptionMessages;
import com.qualityinspection.swequalityinspection.model.entities.ChecklistResultEntity;
import com.qualityinspection.swequalityinspection.model.entities.DefectReportEntity;
import com.qualityinspection.swequalityinspection.model.enums.DefectStatus;
import com.qualityinspection.swequalityinspection.model.mappers.DefectReportMapper;
import com.qualityinspection.swequalityinspection.model.requestDto.DefectReportRequest;
import com.qualityinspection.swequalityinspection.model.responseDto.DefectReportResponseDto;
import com.qualityinspection.swequalityinspection.repository.ChecklistResultRepository;
import com.qualityinspection.swequalityinspection.repository.DefectReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefectReportServiceTest {

    @Mock
    DefectReportRepository reportRepository;

    @Mock
    ChecklistResultRepository checklistResultRepository;

    @Mock
    DefectReportMapper mapper;


    @InjectMocks
    DefectReportService defectReportService;

    ChecklistResultEntity checklistResultEntity;
    DefectReportEntity defectReportEntity;
    DefectReportResponseDto defectReportResponseDto;
    DefectReportRequest defectReportRequest;

    @BeforeEach
    void setUp() {
        checklistResultEntity = new ChecklistResultEntity();
        checklistResultEntity.setChecklistResultId(1L);
        String userEntity = "userEntity";

        defectReportEntity = new DefectReportEntity(
                checklistResultEntity,
                userEntity,
                "Defect description",
                DefectStatus.MINOR
        );
        defectReportEntity.setDefectId(1L);
        defectReportEntity.setCreatedAt(LocalDateTime.now());
        defectReportEntity.setUpdatedAt(LocalDateTime.now());

        defectReportRequest = new DefectReportRequest(
                userEntity,              // userId
                1,              // checkResultId
                DefectStatus.MINOR,
                "Defect description"
        );

        defectReportResponseDto = new DefectReportResponseDto(
                1L,
                checklistResultEntity.getChecklistResultId(),
                userEntity,
                "Defect description",
                DefectStatus.MINOR.name(),
                null,
                defectReportEntity.getCreatedAt(),
                defectReportEntity.getUpdatedAt()
        );
    }

    @Test
    void createDefectReport_Success() {
        when(checklistResultRepository.findById(defectReportRequest.checkResultId()))
                .thenReturn(Optional.of(checklistResultEntity));
        when(reportRepository.existsByChecklistResult(checklistResultEntity))
                .thenReturn(false);
        when(reportRepository.save(any(DefectReportEntity.class)))
                .thenReturn(defectReportEntity);
        when(mapper.toDto(defectReportEntity)).thenReturn(defectReportResponseDto);

        DefectReportResponseDto result = defectReportService.createDefectReport(defectReportRequest);

        assertNotNull(result);
        assertEquals("Defect description", result.description());
        assertEquals(DefectStatus.MINOR.name(), result.status());
        verify(reportRepository).save(any(DefectReportEntity.class));
    }

    @Test
    void createDefectReport_alreadyExists_throwsConflict() {
        when(checklistResultRepository.findById(defectReportRequest.checkResultId()))
                .thenReturn(Optional.of(checklistResultEntity));
        when(reportRepository.existsByChecklistResult(checklistResultEntity))
                .thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> defectReportService.createDefectReport(defectReportRequest));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertTrue(ex.getReason().contains(ExceptionMessages.ResultAlreadyReported));
    }

    @Test
    void getDefectsByProductType_ReturnsData() {
        List<Object[]> rawData = new ArrayList<>();
        rawData.add(new Object[]{"Electronics", 5L});
        when(reportRepository.findDefectsGroupedByProduct()).thenReturn(rawData);

        List<Map<String, Object>> result = defectReportService.getDefectsByProductType();

        assertEquals(1, result.size());
        assertEquals("Electronics", result.get(0).get("productType"));
        assertEquals(5, result.get(0).get("defects_count"));
    }

    @Test
    void getDefectsOverTime_ReturnsData() {
        List<Object[]> rawData = Arrays.<Object[]>asList(new Object[]{"Jul", 7, 3L});
        when(reportRepository.findDefectsOverTime()).thenReturn(rawData);

        List<Map<String, Object>> result = defectReportService.getDefectsOverTime();

        assertEquals(1, result.size());
        assertEquals("Jul", result.get(0).get("month"));
        assertEquals(3, result.get(0).get("defects"));
    }

    @Test
    void getDefectReport_WithResultId() {
        Page<DefectReportEntity> page = new PageImpl<>(List.of(defectReportEntity));
        when(checklistResultRepository.findById(1)).thenReturn(Optional.of(checklistResultEntity));
        when(reportRepository.findByChecklistResult(eq(checklistResultEntity), any(Pageable.class))).thenReturn(page);
        when(mapper.toDto(defectReportEntity)).thenReturn(defectReportResponseDto);

        Page<DefectReportResponseDto> result = defectReportService.getDefectReport(0, 10, new String[]{"createdAt", "DESC"}, 1);

        assertEquals(1, result.getTotalElements());
        assertEquals("Defect description", result.getContent().get(0).description());
    }

    @Test
    void getDefectReport_WithoutResultId() {
        Page<DefectReportEntity> page = new PageImpl<>(List.of(defectReportEntity));
        when(reportRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(mapper.toDto(defectReportEntity)).thenReturn(defectReportResponseDto);

        Page<DefectReportResponseDto> result = defectReportService.getDefectReport(0, 10, new String[]{"createdAt", "DESC"}, null);

        assertEquals(1, result.getTotalElements());
        assertEquals("Defect description", result.getContent().get(0).description());
    }
}
