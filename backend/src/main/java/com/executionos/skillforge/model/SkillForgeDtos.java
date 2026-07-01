package com.executionos.skillforge.model;

import com.executionos.skillforge.model.SkillForgeEnums.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Request and response records used by the SkillForge REST API.
 */
public final class SkillForgeDtos {
    private SkillForgeDtos() {}

    public record RegisterOrganizationRequest(
            @NotBlank String organizationName,
            @NotBlank String slug,
            @Email @NotBlank String adminEmail,
            @NotBlank String adminName,
            String adminPassword
    ) {}

    public record OrganizationResponse(UUID id, String name, String slug, OrganizationStatus status, String primaryColor, String secondaryColor, String accentColor) {}

    public record UserRequest(@NotNull UUID organizationId, @Email @NotBlank String email, @NotBlank String fullName, @NotNull UserRole role, UUID departmentId, UUID batchId, String phone, String externalRef) {}
    public record UserResponse(UUID id, UUID organizationId, String email, String fullName, UserRole role, UserStatus status) {}

    public record DepartmentRequest(@NotNull UUID organizationId, @NotBlank String name, String code) {}
    public record DepartmentResponse(UUID id, UUID organizationId, String name, String code, boolean active) {}

    public record BatchRequest(@NotNull UUID organizationId, UUID departmentId, @NotBlank String name, String code, LocalDate startsOn, LocalDate endsOn) {}
    public record BatchResponse(UUID id, UUID organizationId, UUID departmentId, String name, String code, boolean active) {}

    public record QuestionRequest(
            @NotNull UUID organizationId,
            UUID subjectId,
            @NotBlank String code,
            @NotNull QuestionType type,
            @NotNull Difficulty difficulty,
            String tagsJson,
            @Positive Integer expectedTimeSeconds,
            BigDecimal defaultMarks,
            BigDecimal negativeMarks,
            @NotBlank String title,
            @NotBlank String prompt,
            String optionsJson,
            String correctAnswerJson,
            String explanation,
            String referencesJson,
            String scoringJson
    ) {}

    public record QuestionResponse(UUID id, UUID organizationId, String code, QuestionType type, Difficulty difficulty, QuestionStatus status, UUID currentVersionId) {}
    public record ApprovalRequest(UUID reviewerId, String notes) {}

    public record AssessmentRequest(
            @NotNull UUID organizationId,
            UUID templateId,
            @NotBlank String title,
            String description,
            Integer durationMinutes,
            BigDecimal passingPercentage,
            Instant startAt,
            Instant endAt,
            Integer gracePeriodMinutes,
            Integer candidateLimit,
            Boolean shuffleQuestions,
            Boolean showResultImmediately,
            UUID createdBy
    ) {}

    public record AssessmentResponse(UUID id, UUID organizationId, String title, AssessmentStatus status, Integer durationMinutes, BigDecimal passingPercentage, Instant startAt, Instant endAt) {}
    public record SectionRequest(@NotBlank String name, String instructions, Integer sortOrder, Integer durationMinutes, BigDecimal totalMarks, String rulesJson) {}
    public record AddQuestionRequest(@NotNull UUID questionId, BigDecimal marks, BigDecimal negativeMarks, Integer sortOrder) {}
    public record InviteRequest(@NotNull List<UUID> candidateUserIds, Instant expiresAt) {}
    public record InviteResponse(UUID invitationId, UUID candidateUserId, String tokenPreview, Instant expiresAt) {}

    public record ValidateLinkRequest(@NotBlank String token) {}
    public record ValidateLinkResponse(boolean valid, UUID invitationId, UUID assessmentId, UUID candidateUserId, String status) {}
    public record AnswerRequest(String answerJson, boolean flaggedForReview) {}
    public record EventRequest(@NotBlank String eventType, EventSeverity severity, String detailsJson) {}
    public record AttemptResponse(UUID id, UUID assessmentId, UUID candidateUserId, AttemptStatus status, BigDecimal score, BigDecimal percentage, Boolean passed, BigDecimal suspiciousScore) {}

    public record LiveDashboardResponse(long notStarted, long inProgress, long submitted, long evaluated, List<SfAttempt> attempts) {}
    public record HealthDashboardResponse(String status, long organizations, long trainers, long candidates, long draftQuestions, long approvedQuestions, long publishedAssessments) {}
    public record CatalogQuestion(String id, String subject, String topic, QuestionType type, Difficulty difficulty, String prompt, String correctAnswer, String explanation, int expectedTimeSeconds) {}
    public record SubjectCoverage(String subject, String slug, long totalQuestions, long easy, long medium, long hard) {}
    public record DemoBootstrapResponse(UUID organizationId, String organizationName, long generatedQuestionCoverage, String trainerEmail, String candidateEmail) {}
}
