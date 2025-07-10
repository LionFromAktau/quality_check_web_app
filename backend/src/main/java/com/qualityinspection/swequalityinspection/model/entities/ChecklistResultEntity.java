package com.qualityinspection.swequalityinspection.model.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.qualityinspection.swequalityinspection.model.enums.CheckResultStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "checklist_result", schema = "public")
public class ChecklistResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "checklist_result_id")
    private Long checklistResultId;


    @Column(name = "user_id", nullable = false)
    private String userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "batch_id", nullable = false)
    private BatchEntity batch;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private CheckResultStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "result", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<ChecklistAnswerEntity> answers;

    public ChecklistResultEntity() {}

    public ChecklistResultEntity(String userId, BatchEntity batch, CheckResultStatus status) {
        this.userId = userId;
        this.batch = batch;
        this.status = status;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
    }


    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // === Getters and setters ===

    public Long getChecklistResultId() {
        return checklistResultId;
    }

    public void setChecklistResultId(Long checklistResultId) {
        this.checklistResultId = checklistResultId;
    }

    public String getUser() {
        return userId;
    }

    public void setUser(String userid) {
        this.userId = userid;
    }

    public BatchEntity getBatch() {
        return batch;
    }

    public void setBatch(BatchEntity batch) {
        this.batch = batch;
    }

    public CheckResultStatus getStatus() {
        return status;
    }

    public void setStatus(CheckResultStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<ChecklistAnswerEntity> getAnswers() {
        return answers;
    }

    public void setAnswers(Set<ChecklistAnswerEntity> answers) {
        this.answers = answers;
    }
}
