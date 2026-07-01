package com.executionos.skillforge.model;

import com.executionos.skillforge.model.SkillForgeEnums.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repositories for SkillForge aggregate persistence.
 */
interface SfOrganizationRepository extends JpaRepository<SfOrganization, UUID> {
    boolean existsBySlug(String slug);
    Optional<SfOrganization> findBySlug(String slug);
}

interface SfUserRepository extends JpaRepository<SfUser, UUID> {
    Page<SfUser> findByOrganizationIdAndRole(UUID organizationId, UserRole role, Pageable pageable);
    Optional<SfUser> findByOrganizationIdAndEmail(UUID organizationId, String email);
    long countByOrganizationIdAndRole(UUID organizationId, UserRole role);
}

interface SfDepartmentRepository extends JpaRepository<SfDepartment, UUID> {
    Page<SfDepartment> findByOrganizationId(UUID organizationId, Pageable pageable);
}

interface SfBatchRepository extends JpaRepository<SfBatch, UUID> {
    Page<SfBatch> findByOrganizationId(UUID organizationId, Pageable pageable);
}

interface SfCandidateProfileRepository extends JpaRepository<SfCandidateProfile, UUID> {
    Optional<SfCandidateProfile> findByOrganizationIdAndUserId(UUID organizationId, UUID userId);
    long countByOrganizationIdAndBatchId(UUID organizationId, UUID batchId);
}

interface SfSubjectRepository extends JpaRepository<SfSubject, UUID> {
    List<SfSubject> findByOrganizationIdIsNullOrOrganizationId(UUID organizationId);
}

interface SfQuestionRepository extends JpaRepository<SfQuestion, UUID> {
    Page<SfQuestion> findByOrganizationId(UUID organizationId, Pageable pageable);
    Page<SfQuestion> findByOrganizationIdAndStatus(UUID organizationId, QuestionStatus status, Pageable pageable);
    long countByOrganizationIdAndStatus(UUID organizationId, QuestionStatus status);
}

interface SfQuestionVersionRepository extends JpaRepository<SfQuestionVersion, UUID> {
    List<SfQuestionVersion> findByQuestionIdOrderByVersionNumberDesc(UUID questionId);
}

interface SfQuestionApprovalRepository extends JpaRepository<SfQuestionApproval, UUID> {
    Optional<SfQuestionApproval> findTopByQuestionIdOrderBySubmittedAtDesc(UUID questionId);
}

interface SfAssessmentTemplateRepository extends JpaRepository<SfAssessmentTemplate, UUID> {
    Page<SfAssessmentTemplate> findByOrganizationId(UUID organizationId, Pageable pageable);
}

interface SfAssessmentRepository extends JpaRepository<SfAssessment, UUID> {
    Page<SfAssessment> findByOrganizationId(UUID organizationId, Pageable pageable);
    long countByOrganizationIdAndStatus(UUID organizationId, AssessmentStatus status);
}

interface SfAssessmentSectionRepository extends JpaRepository<SfAssessmentSection, UUID> {
    List<SfAssessmentSection> findByAssessmentIdOrderBySortOrder(UUID assessmentId);
}

interface SfAssessmentQuestionRepository extends JpaRepository<SfAssessmentQuestion, UUID> {
    List<SfAssessmentQuestion> findByAssessmentIdOrderBySortOrder(UUID assessmentId);
}

interface SfAssessmentInvitationRepository extends JpaRepository<SfAssessmentInvitation, UUID> {
    Optional<SfAssessmentInvitation> findByTokenHash(String tokenHash);
    List<SfAssessmentInvitation> findByAssessmentId(UUID assessmentId);
}

interface SfAttemptRepository extends JpaRepository<SfAttempt, UUID> {
    List<SfAttempt> findByAssessmentId(UUID assessmentId);
    Page<SfAttempt> findByOrganizationIdAndCandidateUserId(UUID organizationId, UUID candidateUserId, Pageable pageable);
    long countByAssessmentIdAndStatus(UUID assessmentId, AttemptStatus status);
}

interface SfAttemptAnswerRepository extends JpaRepository<SfAttemptAnswer, UUID> {
    Optional<SfAttemptAnswer> findByAttemptIdAndQuestionId(UUID attemptId, UUID questionId);
    List<SfAttemptAnswer> findByAttemptId(UUID attemptId);
}

interface SfAttemptEventRepository extends JpaRepository<SfAttemptEvent, UUID> {
    List<SfAttemptEvent> findByAttemptIdOrderByOccurredAtDesc(UUID attemptId);
    long countByAttemptId(UUID attemptId);
}
