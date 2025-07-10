package com.qualityinspection.swequalityinspection.model.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "checklist_answer")
public class ChecklistAnswerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "checklist_answer_id")
    private Long checklistAnswerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id", nullable = false)
    @JsonBackReference // Prevent recursion during JSON serialization
    private ChecklistResultEntity result;

    @Column(name = "item_description", nullable = false)
    private String itemDescription;

    @Column(name = "value")
    private Boolean value;

    @Column(name = "comment", columnDefinition = "text")
    private String comment;

    @Column(name = "media_url", columnDefinition = "text")
    private String mediaUrl;

    public ChecklistAnswerEntity() {}

    public ChecklistAnswerEntity(ChecklistResultEntity result, String itemDescription, Boolean value, String comment, String mediaUrl) {
        this.result = result;
        this.itemDescription = itemDescription;
        this.value = value;
        this.comment = comment;
        this.mediaUrl = mediaUrl;
    }

    // Getters and Setters

    public Long getChecklistAnswerId() {
        return checklistAnswerId;
    }

    public void setChecklistAnswerId(Long checklistAnswerId) {
        this.checklistAnswerId = checklistAnswerId;
    }

    public ChecklistResultEntity getResult() {
        return result;
    }

    public void setResult(ChecklistResultEntity result) {
        this.result = result;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
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

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }
}
