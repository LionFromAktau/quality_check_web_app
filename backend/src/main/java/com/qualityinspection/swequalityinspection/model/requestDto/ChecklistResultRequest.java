package com.qualityinspection.swequalityinspection.model.requestDto;

import com.qualityinspection.swequalityinspection.model.enums.CheckResultStatus;

import java.util.List;

public class ChecklistResultRequest {
    private Integer batchId;
    private String userId;
    private List<ChecklistAnswerRequest> checklistAnswers;

    public ChecklistResultRequest() {}

    public ChecklistResultRequest(Integer batchId, String userId, List<ChecklistAnswerRequest> checklistAnswers) {
        this.batchId = batchId;
        this.userId = userId;
        this.checklistAnswers = checklistAnswers;
    }

    public Integer getBatchId() {
        return batchId;
    }

    public void setBatchId(Integer batchId) {
        this.batchId = batchId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<ChecklistAnswerRequest> getChecklistAnswers() {
        return checklistAnswers;
    }

    public void setChecklistAnswers(List<ChecklistAnswerRequest> checklistAnswers) {
        this.checklistAnswers = checklistAnswers;
    }
}