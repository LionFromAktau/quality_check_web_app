package com.qualityinspection.swequalityinspection.model.requestDto;

import org.springframework.web.multipart.MultipartFile;


public class ChecklistAnswerRequest {
    private Integer checklistItemId;
    private Boolean value;
    private String comment;
    private MultipartFile media; // optional image

    // Getters and Setters (or use Lombok)

    public Integer getChecklistItemId() {
        return checklistItemId;
    }

    public void setChecklistItemId(Integer checklistItemId) {
        this.checklistItemId = checklistItemId;
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public MultipartFile getMedia() {
        return media;
    }

    public void setMedia(MultipartFile media) {
        this.media = media;
    }
}

