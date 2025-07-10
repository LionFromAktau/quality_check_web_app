package com.qualityinspection.swequalityinspection.model.entities;

import com.qualityinspection.swequalityinspection.model.enums.DefectStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDateTime;

@Entity
@Table(name = "defect_reports", schema = "public")
public class DefectReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "defect_id")
    private Long defectId;

    @OneToOne
    @JoinColumn(name = "checklist_result_id", nullable = false)
    private ChecklistResultEntity checklistResult;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "description", length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(columnDefinition = "check_result_status", nullable = false)
    private DefectStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public DefectReportEntity(ChecklistResultEntity checklistResult, String userId, String description, DefectStatus status) {
        this.checklistResult = checklistResult;
        this.userId = userId;
        this.description = description;
        this.status = status;
    }

    public DefectReportEntity() {

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

    // Getters and Setters

    public Long getDefectId() {
        return defectId;
    }

    public void setDefectId(Long defectId) {
        this.defectId = defectId;
    }

    public ChecklistResultEntity getChecklistResultId() {
        return checklistResult;
    }

    public void setChecklistResultId(ChecklistResultEntity checklistResultId) {
        this.checklistResult = checklistResultId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DefectStatus getStatus() {
        return status;
    }

    public void setStatus(DefectStatus status) {
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
}

