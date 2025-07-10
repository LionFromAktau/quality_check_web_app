package com.qualityinspection.swequalityinspection.service;

import com.qualityinspection.swequalityinspection.exceptions.ExceptionMessages;
import com.qualityinspection.swequalityinspection.model.entities.*;
import com.qualityinspection.swequalityinspection.model.enums.BatchStatus;
import com.qualityinspection.swequalityinspection.model.enums.CheckResultStatus;
import com.qualityinspection.swequalityinspection.model.requestDto.ChecklistAnswerRequest;
import com.qualityinspection.swequalityinspection.model.requestDto.ChecklistResultRequest;
import com.qualityinspection.swequalityinspection.model.responseDto.ChecklistAnswerResponseDto;
import com.qualityinspection.swequalityinspection.model.responseDto.ChecklistFailedResultResponseDto;
import com.qualityinspection.swequalityinspection.model.responseDto.ChecklistMonthlyStatDto;
import com.qualityinspection.swequalityinspection.model.responseDto.ChecklistYearlyStatsDto;
import com.qualityinspection.swequalityinspection.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChecklistResultService {

    private final ChecklistResultRepository checklistResultRepository;
    private final BatchRepository batchRepository;
    private final ChecklistItemRepository checklistItemRepository;
    private final ChecklistAnswerRepository checklistAnswerRepository;

    public ChecklistResultService(
            ChecklistResultRepository checklistResultRepository,
            BatchRepository batchRepository,
            ChecklistItemRepository checklistItemRepository,
            ChecklistAnswerRepository checklistAnswerRepository
    ) {
        this.checklistResultRepository = checklistResultRepository;
        this.batchRepository = batchRepository;
        this.checklistItemRepository = checklistItemRepository;
        this.checklistAnswerRepository = checklistAnswerRepository;

    }

//    Saves ChecklistResult with answers, make batch status Checked
    public List<ChecklistAnswerResponseDto> createChecklistResult(ChecklistResultRequest checklistResultRequest) {
        BatchEntity batch = batchRepository.findById(checklistResultRequest.getBatchId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ExceptionMessages.BatchNotFound));
        if(batch.getStatus() == BatchStatus.CHECKED){
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, ExceptionMessages.BatchAlreadyChecked
            );
        }

        String user = checklistResultRequest.getUserId();

        CheckResultStatus status = CheckResultStatus.SUCCESS;
        for (ChecklistAnswerRequest i : checklistResultRequest.getChecklistAnswers()) {
            if (!i.getValue()) {
                status = CheckResultStatus.FAIL;
                break;
            }
        }
        ChecklistResultEntity checklistResultEntity = checklistResultRepository.save(new ChecklistResultEntity(user, batch, status));

        List<ChecklistAnswerEntity> answerEntities = checklistResultRequest.getChecklistAnswers().stream()
                .map(answer -> {
                    ChecklistItemEntity item = checklistItemRepository.findById(answer.getChecklistItemId())
                            .orElseThrow(
                                    () -> new ResponseStatusException(
                                            HttpStatus.NOT_FOUND, ExceptionMessages.ChecklistItemNotFound(answer.getChecklistItemId())
                                    )
                            );
                    String mediaUrl = null;
                    MultipartFile media = answer.getMedia();
                    if (media != null && !media.isEmpty()) {
                        try {
                            String fileName = UUID.randomUUID() + "_" + media.getOriginalFilename();
                            Path path = Paths.get("uploads", fileName);
                            Files.createDirectories(path.getParent());
                            Files.copy(media.getInputStream(), path);
                            mediaUrl = "/api/media/" + fileName; // or full URL
                        } catch (IOException e) {
                            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "File upload failed");
                        }
                    }
                    return new ChecklistAnswerEntity(checklistResultEntity, item.getDescription(), answer.getValue(), answer.getComment(), mediaUrl);
                })
                .toList();

        checklistAnswerRepository.saveAll(answerEntities);
        batch.setStatus(BatchStatus.CHECKED);
        batchRepository.save(batch);
        return getChecklistAnswers(checklistResultEntity.getChecklistResultId());
    }

    public List<ChecklistAnswerResponseDto> getChecklistAnswers(Long resultId) {
        return checklistResultRepository.findDetailedAnswers(resultId);
    }
    public List<ChecklistFailedResultResponseDto> getChecklistFailedResults() {
        List<ChecklistResultEntity> failedResults = checklistResultRepository.findFailedWithoutDefectReport();
        List<ChecklistFailedResultResponseDto> dtos = failedResults.stream()
                .map(ChecklistFailedResultResponseDto::fromEntity)
                .toList();
        return dtos;
    }

    public List<ChecklistYearlyStatsDto> getChecklistStats() {
        List<Object[]> raw = checklistResultRepository.findChecklistStatsGroupedByMonthAndStatus();

        Map<Integer, Map<String, ChecklistMonthlyStatDto>> grouped = new HashMap<>();

        for (Object[] row : raw) {
            // Safely extract values with proper casting
            int year = ((Number) row[0]).intValue();                   // year from EXTRACT(YEAR FROM created_at)
            String month = (String) row[2];                            // TO_CHAR(created_at, 'Mon')
            String status = (String) row[3];    // status enum
            long count = ((Number) row[4]).longValue();               // COUNT(*) safely converted

            grouped.putIfAbsent(year, new LinkedHashMap<>());

            ChecklistMonthlyStatDto current = grouped.get(year)
                    .getOrDefault(month, new ChecklistMonthlyStatDto(month, 0, 0, 0));

            if (status.equalsIgnoreCase(CheckResultStatus.SUCCESS.name())) {
                current = new ChecklistMonthlyStatDto(
                        month,
                        (int) count,
                        current.failed(),
                        (int) count + current.failed()
                );
            } else {
                current = new ChecklistMonthlyStatDto(
                        month,
                        current.succeed(),
                        (int) count,
                        (int) count + current.succeed()
                );
            }

            grouped.get(year).put(month, current);
        }

        return grouped.entrySet().stream()
                .map(entry -> new ChecklistYearlyStatsDto(entry.getKey(), new ArrayList<>(entry.getValue().values())))
                .collect(Collectors.toList());
    }
}
