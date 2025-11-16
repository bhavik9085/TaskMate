package com.finalproject.model;

import com.finalproject.util.IdGenerator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Task model for TaskMate application
 */
public class Task {
    private String id;
    private String title;
    private String description;
    private String status; // "To-Do", "In-Progress", "Done"
    private String priority; // "low", "medium", "high"
    private float timeSpent; // total hours logged
    private String startTime; // when timer started
    private String endTime; // when timer stopped
    private List<String> tags;
    private String assignedTo; // team member (optional)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Status constants
    public static final String STATUS_TODO = "To-Do";
    public static final String STATUS_IN_PROGRESS = "In-Progress";
    public static final String STATUS_DONE = "Done";

    // Priority constants
    public static final String PRIORITY_LOW = "low";
    public static final String PRIORITY_MEDIUM = "medium";
    public static final String PRIORITY_HIGH = "high";

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Constructors
    public Task() {
        this.tags = new ArrayList<>();
        this.timeSpent = 0.0f;
        this.status = STATUS_TODO;
        this.priority = PRIORITY_MEDIUM;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Task(String title, String description, String priority) {
        this();
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.id = generateId();
    }

    public Task(String id, String title, String description, String status, String priority,
                float timeSpent, String startTime, String endTime, List<String> tags, String assignedTo) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.timeSpent = timeSpent;
        this.startTime = startTime;
        this.endTime = endTime;
        this.tags = tags != null ? tags : new ArrayList<>();
        this.assignedTo = assignedTo;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Generate unique ID - Simple format: T1, T2, T3, etc.
    private String generateId() {
        return IdGenerator.generateNextTaskId();
    }

    // Start timer
    public void startTimer() {
        this.startTime = LocalDateTime.now().format(formatter);
        this.status = STATUS_IN_PROGRESS;
        this.updatedAt = LocalDateTime.now();
    }

    // Stop timer and calculate time spent
    public void stopTimer() {
        if (this.startTime != null && !this.startTime.isEmpty()) {
            this.endTime = LocalDateTime.now().format(formatter);
            
            // Calculate time difference
            LocalDateTime start = LocalDateTime.parse(this.startTime, formatter);
            LocalDateTime end = LocalDateTime.now();
            
            long minutes = java.time.Duration.between(start, end).toMinutes();
            float hours = minutes / 60.0f;
            
            this.timeSpent += hours;
            this.startTime = null; // Reset timer
            this.updatedAt = LocalDateTime.now();
        }
    }

    // Pause timer (stop without moving to Done)
    public void pauseTimer() {
        if (this.startTime != null && !this.startTime.isEmpty()) {
            stopTimer(); // Same logic as stop, but status might remain In-Progress
        }
    }

    // Move task to different status
    public void moveToStatus(String newStatus) {
        // If timer is running and moving away from In-Progress, stop it
        if (this.status.equals(STATUS_IN_PROGRESS) && this.startTime != null) {
            pauseTimer();
        }
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

    // Check if timer is currently running
    public boolean isTimerRunning() {
        return this.startTime != null && !this.startTime.isEmpty() && this.endTime == null;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
        this.updatedAt = LocalDateTime.now();
    }

    public float getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(float timeSpent) {
        this.timeSpent = timeSpent;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags != null ? tags : new ArrayList<>();
        this.updatedAt = LocalDateTime.now();
    }

    public void addTag(String tag) {
        if (tag != null && !tag.trim().isEmpty() && !this.tags.contains(tag)) {
            this.tags.add(tag);
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void removeTag(String tag) {
        this.tags.remove(tag);
        this.updatedAt = LocalDateTime.now();
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
        this.updatedAt = LocalDateTime.now();
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

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", status='" + status + '\'' +
                ", priority='" + priority + '\'' +
                ", timeSpent=" + String.format("%.2f", timeSpent) + "h" +
                ", assignedTo='" + (assignedTo != null ? assignedTo : "Unassigned") + '\'' +
                ", tags=" + tags +
                '}';
    }

    // Get formatted time spent
    public String getFormattedTimeSpent() {
        return String.format("%.2f", timeSpent) + "h";
    }
}

