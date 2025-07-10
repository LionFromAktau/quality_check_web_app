package com.qualityinspection.swequalityinspection.service;

import com.qualityinspection.swequalityinspection.model.enums.CheckResultStatus;
import com.qualityinspection.swequalityinspection.model.responseDto.ChecklistMonthlyStatDto;
import com.qualityinspection.swequalityinspection.model.responseDto.ChecklistYearlyStatsDto;
import com.qualityinspection.swequalityinspection.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChecklistResultServiceTest {

    private ChecklistResultRepository checklistResultRepository;
    private ChecklistResultService checklistResultService;

    @BeforeEach
    void setUp() {
        checklistResultRepository = mock(ChecklistResultRepository.class);
        checklistResultService = new ChecklistResultService(
                checklistResultRepository,
                mock(BatchRepository.class),
                mock(ChecklistItemRepository.class),
                mock(ChecklistAnswerRepository.class)
        );
    }

    @Test
    void getChecklistStats_ReturnsCorrectMappedData() {
        // Simuliere Daten wie aus der nativen Query
        List<Object[]> mockRawData = List.of(
                new Object[]{2024, 1, "Jan", CheckResultStatus.SUCCESS.name(), 5L},
                new Object[]{2024, 1, "Jan", CheckResultStatus.FAIL.name(), 2L},
                new Object[]{2025, 2, "Feb", CheckResultStatus.SUCCESS.name(), 3L}
        );

        when(checklistResultRepository.findChecklistStatsGroupedByMonthAndStatus())
                .thenReturn(mockRawData);

        List<ChecklistYearlyStatsDto> result = checklistResultService.getChecklistStats();

        assertEquals(2, result.size());

        ChecklistYearlyStatsDto year2024 = result.stream().filter(y -> y.year() == 2024).findFirst().orElse(null);
        assertNotNull(year2024);
        assertEquals(1, year2024.months().size());

        ChecklistMonthlyStatDto janStats = year2024.months().get(0);
        assertEquals("Jan", janStats.month());
        assertEquals(5, janStats.succeed());
        assertEquals(2, janStats.failed());
        assertEquals(7, janStats.total());
    }
}
