package com.project.edusync.em.model.entity;
import com.project.edusync.common.model.AuditableEntity;
import com.project.edusync.em.model.enums.PastExamType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity for examination.past_papers table.
 * This stores metadata for an uploaded PDF past paper.
 */
@Entity
@Table(name = "past_papers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "past_paper_id")),
        @AttributeOverride(name = "createdBy", column = @Column(name = "uploaded_by")), // Remapping createdBy
        @AttributeOverride(name = "createdAt", column = @Column(name = "uploaded_at"))  // Remapping createdAt
})
public class PastPaper extends AuditableEntity {

    // --- Foreign Keys ---

    @Column(name = "class_id", nullable = false)
    private Long classId; // External key to Academics.classes

    @Column(name = "subject_id", nullable = false)
    private Long subjectId; // External key to Academics.subjects

    // --- Columns ---

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "exam_year", nullable = false)
    private Integer examYear;

    @Enumerated(EnumType.STRING)
    @Column(name = "exam_type")
    private PastExamType examType;

    @Column(name = "file_url", nullable = false, length = 1024)
    private String fileUrl;

    @Column(name = "file_mime_type", nullable = false, length = 50)
    private String fileMimeType = "application/pdf";

    @Column(name = "file_size_kb")
    private Integer fileSizeKb;

//    @CreationTimestamp
//    @Column(name = "uploaded_at", nullable = false, updatable = false)
//    private LocalDateTime uploadedAt;
}


