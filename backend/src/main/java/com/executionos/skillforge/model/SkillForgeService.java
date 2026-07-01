package com.executionos.skillforge.model;

import com.executionos.skillforge.model.SkillForgeDtos.*;
import com.executionos.skillforge.model.SkillForgeEnums.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for SkillForge organization, question, assessment, invitation, attempt, and reporting workflows.
 */
@Service
@Transactional
public class SkillForgeService {
    private final SfOrganizationRepository organizations;
    private final SfUserRepository users;
    private final SfDepartmentRepository departments;
    private final SfBatchRepository batches;
    private final SfCandidateProfileRepository candidateProfiles;
    private final SfQuestionRepository questions;
    private final SfQuestionVersionRepository questionVersions;
    private final SfQuestionApprovalRepository approvals;
    private final SfAssessmentRepository assessments;
    private final SfAssessmentSectionRepository sections;
    private final SfAssessmentQuestionRepository assessmentQuestions;
    private final SfAssessmentInvitationRepository invitations;
    private final SfAttemptRepository attempts;
    private final SfAttemptAnswerRepository answers;
    private final SfAttemptEventRepository events;
    private final PasswordEncoder passwordEncoder;

    public SkillForgeService(
            SfOrganizationRepository organizations,
            SfUserRepository users,
            SfDepartmentRepository departments,
            SfBatchRepository batches,
            SfCandidateProfileRepository candidateProfiles,
            SfQuestionRepository questions,
            SfQuestionVersionRepository questionVersions,
            SfQuestionApprovalRepository approvals,
            SfAssessmentRepository assessments,
            SfAssessmentSectionRepository sections,
            SfAssessmentQuestionRepository assessmentQuestions,
            SfAssessmentInvitationRepository invitations,
            SfAttemptRepository attempts,
            SfAttemptAnswerRepository answers,
            SfAttemptEventRepository events,
            PasswordEncoder passwordEncoder) {
        this.organizations = organizations;
        this.users = users;
        this.departments = departments;
        this.batches = batches;
        this.candidateProfiles = candidateProfiles;
        this.questions = questions;
        this.questionVersions = questionVersions;
        this.approvals = approvals;
        this.assessments = assessments;
        this.sections = sections;
        this.assessmentQuestions = assessmentQuestions;
        this.invitations = invitations;
        this.attempts = attempts;
        this.answers = answers;
        this.events = events;
        this.passwordEncoder = passwordEncoder;
    }

    public OrganizationResponse registerOrganization(RegisterOrganizationRequest request) {
        String slug = normalizeSlug(request.slug());
        if (organizations.existsBySlug(slug)) {
            throw new IllegalArgumentException("Organization slug already exists");
        }
        SfOrganization organization = new SfOrganization();
        organization.setName(request.organizationName());
        organization.setSlug(slug);
        organization = organizations.save(organization);

        SfUser admin = new SfUser();
        admin.setOrganizationId(organization.getId());
        admin.setEmail(request.adminEmail().toLowerCase());
        admin.setFullName(request.adminName());
        admin.setRole(UserRole.ORG_ADMIN);
        if (request.adminPassword() != null && !request.adminPassword().isBlank()) {
            admin.setPasswordHash(passwordEncoder.encode(request.adminPassword()));
        }
        users.save(admin);
        return organizationResponse(organization);
    }

    @Transactional(readOnly = true)
    public OrganizationResponse getOrganization(UUID id) {
        return organizationResponse(requireOrganization(id));
    }

    public DepartmentResponse createDepartment(DepartmentRequest request) {
        requireOrganization(request.organizationId());
        SfDepartment department = new SfDepartment();
        department.setOrganizationId(request.organizationId());
        department.setName(request.name());
        department.setCode(request.code());
        return departmentResponse(departments.save(department));
    }

    @Transactional(readOnly = true)
    public Page<SfDepartment> listDepartments(UUID organizationId, Pageable pageable) {
        requireOrganization(organizationId);
        return departments.findByOrganizationId(organizationId, pageable);
    }

    public BatchResponse createBatch(BatchRequest request) {
        requireOrganization(request.organizationId());
        SfBatch batch = new SfBatch();
        batch.setOrganizationId(request.organizationId());
        batch.setDepartmentId(request.departmentId());
        batch.setName(request.name());
        batch.setCode(request.code());
        batch.setStartsOn(request.startsOn());
        batch.setEndsOn(request.endsOn());
        return batchResponse(batches.save(batch));
    }

    @Transactional(readOnly = true)
    public Page<SfBatch> listBatches(UUID organizationId, Pageable pageable) {
        requireOrganization(organizationId);
        return batches.findByOrganizationId(organizationId, pageable);
    }

    public UserResponse createUser(UserRequest request) {
        requireOrganization(request.organizationId());
        users.findByOrganizationIdAndEmail(request.organizationId(), request.email().toLowerCase())
                .ifPresent(existing -> { throw new IllegalArgumentException("User email already exists in organization"); });
        SfUser user = new SfUser();
        user.setOrganizationId(request.organizationId());
        user.setEmail(request.email().toLowerCase());
        user.setFullName(request.fullName());
        user.setRole(request.role());
        user = users.save(user);
        if (request.role() == UserRole.CANDIDATE) {
            SfCandidateProfile profile = new SfCandidateProfile();
            profile.setOrganizationId(request.organizationId());
            profile.setUserId(user.getId());
            profile.setDepartmentId(request.departmentId());
            profile.setBatchId(request.batchId());
            profile.setPhone(request.phone());
            profile.setExternalRef(request.externalRef());
            candidateProfiles.save(profile);
        }
        return userResponse(user);
    }

    @Transactional(readOnly = true)
    public Page<SfUser> listUsers(UUID organizationId, UserRole role, Pageable pageable) {
        requireOrganization(organizationId);
        return role == null ? users.findAll(pageable) : users.findByOrganizationIdAndRole(organizationId, role, pageable);
    }

    public QuestionResponse createQuestion(QuestionRequest request) {
        requireOrganization(request.organizationId());
        SfQuestion question = new SfQuestion();
        question.setOrganizationId(request.organizationId());
        question.setSubjectId(request.subjectId());
        question.setCode(request.code());
        question.setType(request.type());
        question.setDifficulty(request.difficulty());
        question.setTags(defaultJson(request.tagsJson(), "[]"));
        question.setExpectedTimeSeconds(valueOr(request.expectedTimeSeconds(), 60));
        question.setDefaultMarks(valueOr(request.defaultMarks(), BigDecimal.ONE));
        question.setNegativeMarks(valueOr(request.negativeMarks(), BigDecimal.ZERO));
        question = questions.save(question);

        SfQuestionVersion version = buildVersion(question.getId(), 1, request);
        version = questionVersions.save(version);
        question.setCurrentVersionId(version.getId());
        return questionResponse(questions.save(question));
    }

    @Transactional(readOnly = true)
    public Page<SfQuestion> listQuestions(UUID organizationId, QuestionStatus status, Pageable pageable) {
        requireOrganization(organizationId);
        return status == null
                ? questions.findByOrganizationId(organizationId, pageable)
                : questions.findByOrganizationIdAndStatus(organizationId, status, pageable);
    }

    public QuestionResponse submitQuestionForReview(UUID questionId) {
        SfQuestion question = requireQuestion(questionId);
        question.setStatus(QuestionStatus.REVIEW);
        SfQuestionApproval approval = new SfQuestionApproval();
        approval.setQuestionId(question.getId());
        approval.setVersionId(question.getCurrentVersionId());
        approval.setStatus(ApprovalStatus.SUBMITTED);
        approvals.save(approval);
        return questionResponse(questions.save(question));
    }

    public QuestionResponse approveQuestion(UUID questionId, ApprovalRequest request) {
        SfQuestion question = requireQuestion(questionId);
        question.setStatus(QuestionStatus.APPROVED);
        SfQuestionApproval approval = approvals.findTopByQuestionIdOrderBySubmittedAtDesc(questionId).orElseGet(SfQuestionApproval::new);
        approval.setQuestionId(question.getId());
        approval.setVersionId(question.getCurrentVersionId());
        approval.setStatus(ApprovalStatus.APPROVED);
        approval.setReviewedBy(request.reviewerId());
        approval.setReviewNotes(request.notes());
        approval.setReviewedAt(Instant.now());
        approvals.save(approval);
        return questionResponse(questions.save(question));
    }

    public AssessmentResponse createAssessment(AssessmentRequest request) {
        requireOrganization(request.organizationId());
        SfAssessment assessment = new SfAssessment();
        assessment.setOrganizationId(request.organizationId());
        assessment.setTemplateId(request.templateId());
        assessment.setTitle(request.title());
        assessment.setDescription(request.description());
        assessment.setDurationMinutes(valueOr(request.durationMinutes(), 60));
        assessment.setPassingPercentage(valueOr(request.passingPercentage(), new BigDecimal("60.00")));
        assessment.setStartAt(request.startAt());
        assessment.setEndAt(request.endAt());
        assessment.setGracePeriodMinutes(valueOr(request.gracePeriodMinutes(), 0));
        assessment.setCandidateLimit(request.candidateLimit());
        assessment.setShuffleQuestions(valueOr(request.shuffleQuestions(), true));
        assessment.setShowResultImmediately(valueOr(request.showResultImmediately(), false));
        assessment.setCreatedBy(request.createdBy());
        return assessmentResponse(assessments.save(assessment));
    }

    @Transactional(readOnly = true)
    public Page<SfAssessment> listAssessments(UUID organizationId, Pageable pageable) {
        requireOrganization(organizationId);
        return assessments.findByOrganizationId(organizationId, pageable);
    }

    public SfAssessmentSection addSection(UUID assessmentId, SectionRequest request) {
        requireAssessment(assessmentId);
        SfAssessmentSection section = new SfAssessmentSection();
        section.setAssessmentId(assessmentId);
        section.setName(request.name());
        section.setInstructions(request.instructions());
        section.setSortOrder(valueOr(request.sortOrder(), 0));
        section.setDurationMinutes(request.durationMinutes());
        section.setTotalMarks(valueOr(request.totalMarks(), BigDecimal.ZERO));
        section.setRules(defaultJson(request.rulesJson(), "{}"));
        return sections.save(section);
    }

    public SfAssessmentQuestion addQuestionToAssessment(UUID assessmentId, AddQuestionRequest request) {
        SfAssessment assessment = requireAssessment(assessmentId);
        if (assessment.getStatus() != AssessmentStatus.DRAFT) {
            throw new IllegalArgumentException("Only draft assessments can be edited");
        }
        SfQuestion question = requireQuestion(request.questionId());
        if (question.getStatus() != QuestionStatus.APPROVED) {
            throw new IllegalArgumentException("Only approved questions can be added to assessments");
        }
        SfAssessmentQuestion selected = new SfAssessmentQuestion();
        selected.setAssessmentId(assessmentId);
        selected.setQuestionId(question.getId());
        selected.setQuestionVersionId(question.getCurrentVersionId());
        selected.setMarks(valueOr(request.marks(), question.getDefaultMarks()));
        selected.setNegativeMarks(valueOr(request.negativeMarks(), question.getNegativeMarks()));
        selected.setSortOrder(valueOr(request.sortOrder(), 0));
        question.setUsageCount(question.getUsageCount() + 1);
        questions.save(question);
        return assessmentQuestions.save(selected);
    }

    public AssessmentResponse publishAssessment(UUID assessmentId) {
        SfAssessment assessment = requireAssessment(assessmentId);
        if (assessmentQuestions.findByAssessmentIdOrderBySortOrder(assessmentId).isEmpty()) {
            throw new IllegalArgumentException("Assessment requires at least one approved question before publishing");
        }
        assessment.setStatus(AssessmentStatus.PUBLISHED);
        assessment.setPublishedAt(Instant.now());
        return assessmentResponse(assessments.save(assessment));
    }

    public AssessmentResponse scheduleAssessment(UUID assessmentId, AssessmentRequest request) {
        SfAssessment assessment = requireAssessment(assessmentId);
        assessment.setStartAt(request.startAt());
        assessment.setEndAt(request.endAt());
        assessment.setGracePeriodMinutes(valueOr(request.gracePeriodMinutes(), assessment.getGracePeriodMinutes()));
        assessment.setCandidateLimit(request.candidateLimit());
        assessment.setStatus(AssessmentStatus.SCHEDULED);
        return assessmentResponse(assessments.save(assessment));
    }

    public List<InviteResponse> inviteCandidates(UUID assessmentId, InviteRequest request) {
        SfAssessment assessment = requireAssessment(assessmentId);
        Instant expiresAt = request.expiresAt() == null
                ? valueOr(assessment.getEndAt(), Instant.now().plusSeconds(7 * 24 * 3600))
                : request.expiresAt();
        return request.candidateUserIds().stream().map(candidateId -> {
            SfUser candidate = users.findById(candidateId).orElseThrow();
            if (!candidate.getOrganizationId().equals(assessment.getOrganizationId())) {
                throw new IllegalArgumentException("Candidate belongs to a different organization");
            }
            String token = UUID.randomUUID() + "." + UUID.randomUUID();
            SfAssessmentInvitation invitation = new SfAssessmentInvitation();
            invitation.setOrganizationId(assessment.getOrganizationId());
            invitation.setAssessmentId(assessment.getId());
            invitation.setCandidateUserId(candidateId);
            invitation.setTokenHash(hash(token));
            invitation.setExpiresAt(expiresAt);
            invitation.setStatus(InvitationStatus.SENT);
            invitation.setEmailStatus("QUEUED");
            invitation.setSentAt(Instant.now());
            invitation = invitations.save(invitation);
            return new InviteResponse(invitation.getId(), candidateId, token.substring(0, 12) + "...", expiresAt);
        }).toList();
    }

    public ValidateLinkResponse validateLink(ValidateLinkRequest request) {
        SfAssessmentInvitation invitation = invitations.findByTokenHash(hash(request.token()))
                .orElseThrow(() -> new NoSuchElementException("Invitation not found"));
        boolean valid = invitation.getExpiresAt().isAfter(Instant.now()) && invitation.getConsumedAt() == null;
        if (valid && invitation.getOpenedAt() == null) {
            invitation.setOpenedAt(Instant.now());
            invitation.setStatus(InvitationStatus.OPENED);
            invitations.save(invitation);
        }
        return new ValidateLinkResponse(valid, invitation.getId(), invitation.getAssessmentId(), invitation.getCandidateUserId(), invitation.getStatus().name());
    }

    public AttemptResponse startAttempt(UUID invitationId, String ipAddress, String userAgent) {
        SfAssessmentInvitation invitation = invitations.findById(invitationId).orElseThrow();
        if (invitation.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Invitation has expired");
        }
        SfAssessment assessment = requireAssessment(invitation.getAssessmentId());
        SfAttempt attempt = new SfAttempt();
        attempt.setOrganizationId(invitation.getOrganizationId());
        attempt.setAssessmentId(invitation.getAssessmentId());
        attempt.setInvitationId(invitation.getId());
        attempt.setCandidateUserId(invitation.getCandidateUserId());
        attempt.setStatus(AttemptStatus.IN_PROGRESS);
        attempt.setStartedAt(Instant.now());
        attempt.setExpiresAt(Instant.now().plusSeconds((long) assessment.getDurationMinutes() * 60));
        attempt.setIpAddress(ipAddress);
        attempt.setUserAgent(userAgent);
        invitation.setConsumedAt(Instant.now());
        invitation.setStatus(InvitationStatus.CONSUMED);
        invitations.save(invitation);
        return attemptResponse(attempts.save(attempt));
    }

    public SfAttemptAnswer saveAnswer(UUID attemptId, UUID questionId, AnswerRequest request) {
        SfAttempt attempt = requireAttempt(attemptId);
        SfAssessmentQuestion selected = assessmentQuestions.findByAssessmentIdOrderBySortOrder(attempt.getAssessmentId()).stream()
                .filter(item -> item.getQuestionId().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Question is not part of this assessment"));
        SfAttemptAnswer answer = answers.findByAttemptIdAndQuestionId(attemptId, questionId).orElseGet(SfAttemptAnswer::new);
        answer.setAttemptId(attemptId);
        answer.setQuestionId(questionId);
        answer.setQuestionVersionId(selected.getQuestionVersionId());
        answer.setAnswer(defaultJson(request.answerJson(), "{}"));
        answer.setStatus(request.flaggedForReview() ? AnswerStatus.FLAGGED : AnswerStatus.ANSWERED);
        answer.setFlaggedForReview(request.flaggedForReview());
        answer.setAutoSavedAt(Instant.now());
        return answers.save(answer);
    }

    public SfAttemptEvent recordEvent(UUID attemptId, EventRequest request) {
        requireAttempt(attemptId);
        SfAttemptEvent event = new SfAttemptEvent();
        event.setAttemptId(attemptId);
        event.setEventType(request.eventType());
        event.setSeverity(request.severity() == null ? EventSeverity.INFO : request.severity());
        event.setDetails(defaultJson(request.detailsJson(), "{}"));
        return events.save(event);
    }

    public AttemptResponse submitAttempt(UUID attemptId) {
        SfAttempt attempt = requireAttempt(attemptId);
        BigDecimal total = assessmentQuestions.findByAssessmentIdOrderBySortOrder(attempt.getAssessmentId()).stream()
                .map(SfAssessmentQuestion::getMarks)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal awarded = answers.findByAttemptId(attemptId).stream()
                .map(answer -> answer.getAwardedMarks() == null ? BigDecimal.ZERO : answer.getAwardedMarks())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal percentage = total.signum() == 0 ? BigDecimal.ZERO : awarded.multiply(new BigDecimal("100")).divide(total, 2, RoundingMode.HALF_UP);
        SfAssessment assessment = requireAssessment(attempt.getAssessmentId());
        attempt.setStatus(AttemptStatus.SUBMITTED);
        attempt.setSubmittedAt(Instant.now());
        attempt.setScore(awarded);
        attempt.setPercentage(percentage);
        attempt.setPassed(percentage.compareTo(assessment.getPassingPercentage()) >= 0);
        long eventCount = events.countByAttemptId(attemptId);
        attempt.setSuspiciousScore(BigDecimal.valueOf(Math.min(100, eventCount * 5L)));
        return attemptResponse(attempts.save(attempt));
    }

    @Transactional(readOnly = true)
    public LiveDashboardResponse liveDashboard(UUID assessmentId) {
        List<SfAttempt> all = attempts.findByAssessmentId(assessmentId);
        return new LiveDashboardResponse(
                attempts.countByAssessmentIdAndStatus(assessmentId, AttemptStatus.NOT_STARTED),
                attempts.countByAssessmentIdAndStatus(assessmentId, AttemptStatus.IN_PROGRESS),
                attempts.countByAssessmentIdAndStatus(assessmentId, AttemptStatus.SUBMITTED),
                attempts.countByAssessmentIdAndStatus(assessmentId, AttemptStatus.EVALUATED),
                all);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> candidateReport(UUID organizationId, UUID candidateUserId) {
        return Map.of(
                "candidateUserId", candidateUserId,
                "attempts", attempts.findByOrganizationIdAndCandidateUserId(organizationId, candidateUserId, Pageable.unpaged()).getContent());
    }

    @Transactional(readOnly = true)
    public HealthDashboardResponse healthDashboard(UUID organizationId) {
        return new HealthDashboardResponse(
                "ok",
                organizations.count(),
                users.countByOrganizationIdAndRole(organizationId, UserRole.TRAINER),
                users.countByOrganizationIdAndRole(organizationId, UserRole.CANDIDATE),
                questions.countByOrganizationIdAndStatus(organizationId, QuestionStatus.DRAFT),
                questions.countByOrganizationIdAndStatus(organizationId, QuestionStatus.APPROVED),
                assessments.countByOrganizationIdAndStatus(organizationId, AssessmentStatus.PUBLISHED));
    }

    @Transactional(readOnly = true)
    public List<SubjectCoverage> catalogCoverage() {
        return subjectNames().stream()
                .map(subject -> new SubjectCoverage(subject, normalizeSlug(subject), 1200, 400, 400, 400))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CatalogQuestion> generatedCatalog(String subject, Difficulty difficulty, int page, int size) {
        String normalizedSubject = subject == null || subject.isBlank() ? "Java" : subject;
        Difficulty selectedDifficulty = difficulty == null ? Difficulty.EASY : difficulty;
        int safePage = Math.max(0, page);
        int safeSize = Math.min(Math.max(size, 1), 100);
        int start = safePage * safeSize + 1;
        return IntStream.range(start, start + safeSize)
                .mapToObj(index -> catalogQuestion(normalizedSubject, selectedDifficulty, index))
                .toList();
    }

    public DemoBootstrapResponse bootstrapDemo() {
        SfOrganization organization = organizations.findBySlug("apex-learning-cloud").orElseGet(() -> {
            SfOrganization created = new SfOrganization();
            created.setName("Apex Learning Cloud");
            created.setSlug("apex-learning-cloud");
            return organizations.save(created);
        });
        users.findByOrganizationIdAndEmail(organization.getId(), "trainer@apex.example").orElseGet(() -> {
            SfUser trainer = new SfUser();
            trainer.setOrganizationId(organization.getId());
            trainer.setEmail("trainer@apex.example");
            trainer.setFullName("Apex Trainer");
            trainer.setRole(UserRole.TRAINER);
            return users.save(trainer);
        });
        users.findByOrganizationIdAndEmail(organization.getId(), "candidate@apex.example").orElseGet(() -> {
            SfUser candidate = new SfUser();
            candidate.setOrganizationId(organization.getId());
            candidate.setEmail("candidate@apex.example");
            candidate.setFullName("Apex Candidate");
            candidate.setRole(UserRole.CANDIDATE);
            return users.save(candidate);
        });
        return new DemoBootstrapResponse(organization.getId(), organization.getName(), subjectNames().size() * 1200L, "trainer@apex.example", "candidate@apex.example");
    }

    private SfOrganization requireOrganization(UUID id) {
        return organizations.findById(id).orElseThrow();
    }

    private SfQuestion requireQuestion(UUID id) {
        return questions.findById(id).orElseThrow();
    }

    private SfAssessment requireAssessment(UUID id) {
        return assessments.findById(id).orElseThrow();
    }

    private SfAttempt requireAttempt(UUID id) {
        return attempts.findById(id).orElseThrow();
    }

    private SfQuestionVersion buildVersion(UUID questionId, int versionNumber, QuestionRequest request) {
        SfQuestionVersion version = new SfQuestionVersion();
        version.setQuestionId(questionId);
        version.setVersionNumber(versionNumber);
        version.setTitle(request.title());
        version.setPrompt(request.prompt());
        version.setOptions(defaultJson(request.optionsJson(), "[]"));
        version.setCorrectAnswer(defaultJson(request.correctAnswerJson(), "{}"));
        version.setExplanation(request.explanation());
        version.setReferences(defaultJson(request.referencesJson(), "[]"));
        version.setScoring(defaultJson(request.scoringJson(), "{}"));
        return version;
    }

    private OrganizationResponse organizationResponse(SfOrganization organization) {
        return new OrganizationResponse(organization.getId(), organization.getName(), organization.getSlug(), organization.getStatus(), organization.getPrimaryColor(), organization.getSecondaryColor(), organization.getAccentColor());
    }

    private DepartmentResponse departmentResponse(SfDepartment department) {
        return new DepartmentResponse(department.getId(), department.getOrganizationId(), department.getName(), department.getCode(), department.isActive());
    }

    private BatchResponse batchResponse(SfBatch batch) {
        return new BatchResponse(batch.getId(), batch.getOrganizationId(), batch.getDepartmentId(), batch.getName(), batch.getCode(), batch.isActive());
    }

    private UserResponse userResponse(SfUser user) {
        return new UserResponse(user.getId(), user.getOrganizationId(), user.getEmail(), user.getFullName(), user.getRole(), user.getStatus());
    }

    private QuestionResponse questionResponse(SfQuestion question) {
        return new QuestionResponse(question.getId(), question.getOrganizationId(), question.getCode(), question.getType(), question.getDifficulty(), question.getStatus(), question.getCurrentVersionId());
    }

    private AssessmentResponse assessmentResponse(SfAssessment assessment) {
        return new AssessmentResponse(assessment.getId(), assessment.getOrganizationId(), assessment.getTitle(), assessment.getStatus(), assessment.getDurationMinutes(), assessment.getPassingPercentage(), assessment.getStartAt(), assessment.getEndAt());
    }

    private AttemptResponse attemptResponse(SfAttempt attempt) {
        return new AttemptResponse(attempt.getId(), attempt.getAssessmentId(), attempt.getCandidateUserId(), attempt.getStatus(), attempt.getScore(), attempt.getPercentage(), attempt.getPassed(), attempt.getSuspiciousScore());
    }

    private String normalizeSlug(String value) {
        return value.toLowerCase().trim().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
    }

    private String defaultJson(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private <T> T valueOr(T value, T fallback) {
        return value == null ? fallback : value;
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to hash value", ex);
        }
    }

    private CatalogQuestion catalogQuestion(String subject, Difficulty difficulty, int index) {
        QuestionType[] types = QuestionType.values();
        String topic = topicFor(subject, index);
        QuestionType type = types[index % types.length];
        String id = normalizeSlug(subject) + "-" + difficulty.name().toLowerCase() + "-" + String.format("%04d", index);
        String prompt = "In " + subject + ", evaluate this " + difficulty.name().toLowerCase() + " " + topic + " scenario and select the best enterprise-ready answer. Question #" + index + ".";
        String answer = switch (type) {
            case TRUE_FALSE -> "True";
            case MULTIPLE_SELECT -> "[\"Option A\", \"Option C\"]";
            case ORDERING -> "[\"Validate\", \"Execute\", \"Verify\", \"Report\"]";
            default -> "Option A";
        };
        return new CatalogQuestion(id, subject, topic, type, difficulty, prompt, answer, "The answer follows standard " + subject + " operational practice for " + topic + ".", difficulty == Difficulty.HARD ? 180 : difficulty == Difficulty.MEDIUM ? 120 : 60);
    }

    private String topicFor(String subject, int index) {
        String[] topics = {"fundamentals", "troubleshooting", "security", "performance", "operations", "integration", "best practices", "automation"};
        return subject + " " + topics[index % topics.length];
    }

    private List<String> subjectNames() {
        return List.of("Linux", "Unix", "Shell", "Bash", "Splunk", "Java", "Spring Boot", "Spring MVC", "Spring Security", "Hibernate", "JPA", "SQL", "Oracle", "PostgreSQL", "Kafka", "IBM MQ", "REST API", "Microservices", "Docker", "Kubernetes", "Git", "GitHub", "Jenkins", "Maven", "Gradle", "AWS", "Azure", "GCP", "New Relic", "Grafana", "Prometheus", "ServiceNow", "ITIL", "Apigee", "IBM FileNet", "PCF", "RabbitMQ", "Redis", "JSON", "XML", "XPath", "Swagger", "Postman", "REST Assured", "JUnit", "Mockito", "DSA", "OOP", "Collections", "Exception Handling", "Streams", "Multithreading", "Design Patterns");
    }
}
