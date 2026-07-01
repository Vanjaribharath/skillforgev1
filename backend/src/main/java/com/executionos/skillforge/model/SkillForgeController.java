package com.executionos.skillforge.model;

import com.executionos.skillforge.model.SkillForgeDtos.*;
import com.executionos.skillforge.model.SkillForgeEnums.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST API controller for SkillForge Enterprise Phase 3 backend workflows.
 */
@RestController
@RequestMapping("/api/v1/skillforge")
public class SkillForgeController {
    private final SkillForgeService service;

    public SkillForgeController(SkillForgeService service) {
        this.service = service;
    }

    @PostMapping("/auth/register-organization")
    public OrganizationResponse registerOrganization(@Valid @RequestBody RegisterOrganizationRequest request) {
        return service.registerOrganization(request);
    }

    @GetMapping("/organizations/{id}")
    public OrganizationResponse organization(@PathVariable UUID id) {
        return service.getOrganization(id);
    }

    @GetMapping("/departments")
    public Object departments(@RequestParam UUID organizationId, Pageable pageable) {
        return service.listDepartments(organizationId, pageable);
    }

    @PostMapping("/departments")
    public DepartmentResponse createDepartment(@Valid @RequestBody DepartmentRequest request) {
        return service.createDepartment(request);
    }

    @GetMapping("/batches")
    public Object batches(@RequestParam UUID organizationId, Pageable pageable) {
        return service.listBatches(organizationId, pageable);
    }

    @PostMapping("/batches")
    public BatchResponse createBatch(@Valid @RequestBody BatchRequest request) {
        return service.createBatch(request);
    }

    @GetMapping("/users")
    public Object users(@RequestParam UUID organizationId, @RequestParam(required = false) UserRole role, Pageable pageable) {
        return service.listUsers(organizationId, role, pageable);
    }

    @PostMapping("/users")
    public UserResponse createUser(@Valid @RequestBody UserRequest request) {
        return service.createUser(request);
    }

    @GetMapping("/trainers")
    public Object trainers(@RequestParam UUID organizationId, Pageable pageable) {
        return service.listUsers(organizationId, UserRole.TRAINER, pageable);
    }

    @PostMapping("/trainers")
    public UserResponse createTrainer(@Valid @RequestBody UserRequest request) {
        return service.createUser(new UserRequest(request.organizationId(), request.email(), request.fullName(), UserRole.TRAINER, request.departmentId(), request.batchId(), request.phone(), request.externalRef()));
    }

    @GetMapping("/candidates")
    public Object candidates(@RequestParam UUID organizationId, Pageable pageable) {
        return service.listUsers(organizationId, UserRole.CANDIDATE, pageable);
    }

    @PostMapping("/candidates")
    public UserResponse createCandidate(@Valid @RequestBody UserRequest request) {
        return service.createUser(new UserRequest(request.organizationId(), request.email(), request.fullName(), UserRole.CANDIDATE, request.departmentId(), request.batchId(), request.phone(), request.externalRef()));
    }

    @GetMapping("/questions")
    public Object questions(@RequestParam UUID organizationId, @RequestParam(required = false) QuestionStatus status, Pageable pageable) {
        return service.listQuestions(organizationId, status, pageable);
    }

    @PostMapping("/questions")
    public QuestionResponse createQuestion(@Valid @RequestBody QuestionRequest request) {
        return service.createQuestion(request);
    }

    @PostMapping("/questions/{id}/submit-review")
    public QuestionResponse submitQuestionForReview(@PathVariable UUID id) {
        return service.submitQuestionForReview(id);
    }

    @PostMapping("/questions/{id}/approve")
    public QuestionResponse approveQuestion(@PathVariable UUID id, @RequestBody ApprovalRequest request) {
        return service.approveQuestion(id, request);
    }

    @GetMapping("/assessments")
    public Object assessments(@RequestParam UUID organizationId, Pageable pageable) {
        return service.listAssessments(organizationId, pageable);
    }

    @PostMapping("/assessments")
    public AssessmentResponse createAssessment(@Valid @RequestBody AssessmentRequest request) {
        return service.createAssessment(request);
    }

    @PostMapping("/assessments/{id}/sections")
    public SfAssessmentSection addSection(@PathVariable UUID id, @Valid @RequestBody SectionRequest request) {
        return service.addSection(id, request);
    }

    @PostMapping("/assessments/{id}/questions")
    public SfAssessmentQuestion addQuestion(@PathVariable UUID id, @Valid @RequestBody AddQuestionRequest request) {
        return service.addQuestionToAssessment(id, request);
    }

    @PostMapping("/assessments/{id}/publish")
    public AssessmentResponse publishAssessment(@PathVariable UUID id) {
        return service.publishAssessment(id);
    }

    @PostMapping("/assessments/{id}/schedule")
    public AssessmentResponse scheduleAssessment(@PathVariable UUID id, @Valid @RequestBody AssessmentRequest request) {
        return service.scheduleAssessment(id, request);
    }

    @PostMapping("/assessments/{id}/invite")
    public Object inviteCandidates(@PathVariable UUID id, @Valid @RequestBody InviteRequest request) {
        return service.inviteCandidates(id, request);
    }

    @GetMapping("/assessments/{id}/live")
    public LiveDashboardResponse liveDashboard(@PathVariable UUID id) {
        return service.liveDashboard(id);
    }

    @PostMapping("/candidate/link/validate")
    public ValidateLinkResponse validateLink(@Valid @RequestBody ValidateLinkRequest request) {
        return service.validateLink(request);
    }

    @PostMapping("/candidate/attempts/start/{invitationId}")
    public AttemptResponse startAttempt(@PathVariable UUID invitationId, HttpServletRequest request) {
        return service.startAttempt(invitationId, request.getRemoteAddr(), request.getHeader("User-Agent"));
    }

    @PutMapping("/candidate/attempts/{attemptId}/answers/{questionId}")
    public SfAttemptAnswer saveAnswer(@PathVariable UUID attemptId, @PathVariable UUID questionId, @RequestBody AnswerRequest request) {
        return service.saveAnswer(attemptId, questionId, request);
    }

    @PostMapping("/candidate/attempts/{attemptId}/events")
    public SfAttemptEvent recordEvent(@PathVariable UUID attemptId, @Valid @RequestBody EventRequest request) {
        return service.recordEvent(attemptId, request);
    }

    @PostMapping("/candidate/attempts/{attemptId}/submit")
    public AttemptResponse submitAttempt(@PathVariable UUID attemptId) {
        return service.submitAttempt(attemptId);
    }

    @GetMapping("/reports/candidates/{candidateUserId}")
    public Map<String, Object> candidateReport(@RequestParam UUID organizationId, @PathVariable UUID candidateUserId) {
        return service.candidateReport(organizationId, candidateUserId);
    }

    @GetMapping("/health-dashboard")
    public HealthDashboardResponse healthDashboard(@RequestParam UUID organizationId) {
        return service.healthDashboard(organizationId);
    }

    @GetMapping("/catalog/coverage")
    public Object catalogCoverage() {
        return service.catalogCoverage();
    }

    @GetMapping("/catalog/questions")
    public Object catalogQuestions(
            @RequestParam(defaultValue = "Java") String subject,
            @RequestParam(required = false) Difficulty difficulty,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size) {
        return service.generatedCatalog(subject, difficulty, page, size);
    }

    @PostMapping("/demo/bootstrap")
    public DemoBootstrapResponse bootstrapDemo() {
        return service.bootstrapDemo();
    }

    @PostMapping("/exports")
    public ResponseEntity<Map<String, String>> createExportJob() {
        return ResponseEntity.accepted().body(Map.of("status", "accepted", "message", "Export job queue integration is planned for the RabbitMQ worker phase."));
    }

    @GetMapping("/webhooks")
    public Map<String, String> webhooks() {
        return Map.of("status", "planned", "message", "Webhook endpoint persistence is part of the database design and delivery worker implementation remains pending.");
    }
}
