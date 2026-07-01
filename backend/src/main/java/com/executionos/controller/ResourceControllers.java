package com.executionos.controller;

import com.executionos.dto.ExecutionDtos.*;
import com.executionos.model.*;
import com.executionos.model.Enums.FocusStatus;
import com.executionos.repository.*;
import com.executionos.service.CurrentUserService;
import com.executionos.service.ScheduleService;
import com.executionos.service.TaskService;
import com.executionos.service.UploadValidationService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

public final class ResourceControllers {
    private ResourceControllers() {}

    @RestController
    @RequestMapping("/api/v1/tasks")
    public static class TaskController {
        private final TaskService tasks;
        private final CurrentUserService currentUser;
        public TaskController(TaskService tasks, CurrentUserService currentUser) {
            this.tasks = tasks;
            this.currentUser = currentUser;
        }
        @GetMapping Object list(@RequestParam(required = false) Enums.TaskStatus status, Pageable pageable, Principal principal) {
            return tasks.list(principal.getName(), status, pageable);
        }
        @PostMapping TaskResponse create(@Valid @RequestBody TaskRequest request, Principal principal) {
            return tasks.create(currentUser.requireUser(principal), request);
        }
        @GetMapping("/{id}") TaskResponse get(@PathVariable UUID id, Principal principal) {
            return tasks.get(principal.getName(), id);
        }
        @PutMapping("/{id}") TaskResponse update(@PathVariable UUID id, @Valid @RequestBody TaskRequest request, Principal principal) {
            return tasks.update(principal.getName(), id, request);
        }
        @DeleteMapping("/{id}") ResponseEntity<Void> delete(@PathVariable UUID id, Principal principal) {
            tasks.delete(principal.getName(), id);
            return ResponseEntity.noContent().build();
        }
        @PatchMapping("/{id}/status") TaskResponse status(@PathVariable UUID id, @Valid @RequestBody StatusRequest request, Principal principal) {
            return tasks.status(principal.getName(), id, request);
        }
        @PatchMapping("/reorder") ResponseEntity<Void> reorder() { return ResponseEntity.accepted().build(); }
    }

    @RestController
    @RequestMapping("/api/v1/schedules")
    public static class ScheduleController {
        private final ScheduleService schedules;
        private final CurrentUserService currentUser;
        public ScheduleController(ScheduleService schedules, CurrentUserService currentUser) {
            this.schedules = schedules;
            this.currentUser = currentUser;
        }
        @GetMapping Object list(Principal principal) { return schedules.list(principal.getName()); }
        @PostMapping ScheduleResponse create(@Valid @RequestBody ScheduleRequest request, Principal principal) {
            return schedules.create(currentUser.requireUser(principal), request);
        }
        @PutMapping("/{id}") ScheduleResponse update(@PathVariable UUID id, @Valid @RequestBody ScheduleRequest request, Principal principal) {
            return schedules.update(principal.getName(), id, request);
        }
        @DeleteMapping("/{id}") ResponseEntity<Void> delete(@PathVariable UUID id, Principal principal) {
            schedules.delete(principal.getName(), id);
            return ResponseEntity.noContent().build();
        }
    }

    @RestController
    @RequestMapping("/api/v1/focus-sessions")
    public static class FocusController {
        private final FocusSessionRepository repo;
        FocusController(FocusSessionRepository repo) { this.repo = repo; }
        @PostMapping("/start") FocusSession start(@RequestBody FocusSession session) { return repo.save(session); }
        @PatchMapping("/{id}/pause") FocusSession pause(@PathVariable UUID id) { return set(id, FocusStatus.PAUSED); }
        @PatchMapping("/{id}/resume") FocusSession resume(@PathVariable UUID id) { return set(id, FocusStatus.ACTIVE); }
        @PatchMapping("/{id}/complete") FocusSession complete(@PathVariable UUID id) {
            FocusSession session = set(id, FocusStatus.COMPLETED);
            session.setEndTime(Instant.now());
            return repo.save(session);
        }
        @GetMapping Object list(Pageable pageable) { return repo.findAll(pageable); }
        @GetMapping("/stats") Object stats() { return Map.of("deepWorkSeconds", 12600, "sessions", 7, "completionRate", 0.82); }
        private FocusSession set(UUID id, FocusStatus status) { FocusSession s = repo.findById(id).orElseThrow(); s.setStatus(status); return repo.save(s); }
    }

    @RestController
    @RequestMapping("/api/v1/notes")
    public static class NoteController {
        private final NoteRepository repo;
        NoteController(NoteRepository repo) { this.repo = repo; }
        @GetMapping Object list(Pageable pageable) { return repo.findAll(pageable); }
        @PostMapping Note create(@RequestBody Note note) { return repo.save(note); }
        @GetMapping("/{id}") Note get(@PathVariable UUID id) { return repo.findById(id).orElseThrow(); }
        @PutMapping("/{id}") Note update(@PathVariable UUID id, @RequestBody Note note) { return repo.save(note); }
        @DeleteMapping("/{id}") ResponseEntity<Void> delete(@PathVariable UUID id) { repo.deleteById(id); return ResponseEntity.noContent().build(); }
    }

    @RestController
    @RequestMapping("/api/v1/attachments")
    public static class AttachmentController {
        private final AttachmentRepository repo;
        private final CurrentUserService currentUser;
        private final UploadValidationService uploadValidation;
        public AttachmentController(AttachmentRepository repo, CurrentUserService currentUser, UploadValidationService uploadValidation) {
            this.repo = repo;
            this.currentUser = currentUser;
            this.uploadValidation = uploadValidation;
        }
        @PostMapping("/upload") Attachment upload(@RequestParam MultipartFile file, Principal principal) {
            uploadValidation.validate(file);
            Attachment attachment = new Attachment();
            attachment.setUser(currentUser.requireUser(principal));
            attachment.setFileName(file.getOriginalFilename());
            attachment.setFileSize(file.getSize());
            attachment.setFileType(file.getContentType());
            attachment.setStorageUrl("local://" + file.getOriginalFilename());
            return repo.save(attachment);
        }
        @GetMapping("/{id}/download") Attachment download(@PathVariable UUID id) { return repo.findById(id).orElseThrow(); }
        @DeleteMapping("/{id}") ResponseEntity<Void> delete(@PathVariable UUID id) { repo.deleteById(id); return ResponseEntity.noContent().build(); }
    }

    @RestController
    @RequestMapping("/api/v1/knowledge")
    public static class KnowledgeController {
        private final CategoryRepository categories;
        private final KnowledgeItemRepository items;
        KnowledgeController(CategoryRepository categories, KnowledgeItemRepository items) { this.categories = categories; this.items = items; }
        @GetMapping("/categories") Object categories(Pageable pageable) { return categories.findAll(pageable); }
        @PostMapping("/categories") Category createCategory(@RequestBody Category category) { return categories.save(category); }
        @GetMapping("/items") Object items(Pageable pageable) { return items.findAll(pageable); }
        @PostMapping("/items") KnowledgeItem createItem(@RequestBody KnowledgeItem item) { return items.save(item); }
        @GetMapping("/items/{id}") KnowledgeItem item(@PathVariable UUID id) { return items.findById(id).orElseThrow(); }
        @PutMapping("/items/{id}") KnowledgeItem updateItem(@PathVariable UUID id, @RequestBody KnowledgeItem item) { return items.save(item); }
        @DeleteMapping("/items/{id}") ResponseEntity<Void> deleteItem(@PathVariable UUID id) { items.deleteById(id); return ResponseEntity.noContent().build(); }
    }

    @RestController
    @RequestMapping("/api/v1/journal")
    public static class JournalController {
        private final JournalEntryRepository repo;
        JournalController(JournalEntryRepository repo) { this.repo = repo; }
        @GetMapping JournalEntry byDate(@RequestParam LocalDate date) { return repo.findAll().stream().filter(e -> date.equals(e.getEntryDate())).findFirst().orElseThrow(); }
        @PostMapping JournalEntry create(@RequestBody JournalEntry entry) { return repo.save(entry); }
        @PutMapping("/{id}") JournalEntry update(@PathVariable UUID id, @RequestBody JournalEntry entry) { return repo.save(entry); }
        @GetMapping("/entries") Object entries(Pageable pageable) { return repo.findAll(pageable); }
    }

    @RestController
    @RequestMapping("/api/v1")
    public static class ReadModelController {
        @GetMapping("/dashboard/today") Object today() { return Map.of("tasksDone", 5, "tasksTotal", 8, "focusMinutes", 145, "nextBlock", "Build vault search"); }
        @GetMapping("/dashboard/stats") Object dashboardStats() { return Map.of("weeklyFocusHours", 18, "streak", 12, "notes", 34); }
        @GetMapping("/dashboard/streaks") Object streaks() { return Map.of("current", 12, "best", 29); }
        @GetMapping("/analytics/weekly") Object weekly() { return Map.of("days", new int[]{3, 5, 4, 6, 2, 7, 4}); }
        @GetMapping("/analytics/categories") Object categories() { return Map.of("code", 42, "learning", 25, "shipping", 33); }
        @GetMapping("/analytics/deep-work") Object deepWork() { return Map.of("hours", 18, "averageSessionMinutes", 52); }
        @GetMapping("/search") Object search(@RequestParam String q, @RequestParam(required = false) String type) { return Map.of("query", q, "type", type, "results", new Object[]{}); }
    }

    @RestController
    @RequestMapping("/api/v1/admin")
    public static class AdminController {
        private final com.executionos.repository.UserRepository users;
        private final ActivityLogRepository logs;
        AdminController(com.executionos.repository.UserRepository users, ActivityLogRepository logs) { this.users = users; this.logs = logs; }
        @GetMapping("/users") Object users(Pageable pageable) { return users.findAll(pageable); }
        @GetMapping("/logs") Object logs(Pageable pageable) { return logs.findAll(pageable); }
        @GetMapping("/health") Object health() { return Map.of("status", "ok"); }
    }
}
