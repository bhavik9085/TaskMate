package com.finalproject.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Kanban Board model containing tasks organized by status
 */
public class KanbanBoard {
    private List<Task> tasks;

    public KanbanBoard() {
        this.tasks = new ArrayList<>();
    }

    public KanbanBoard(List<Task> tasks) {
        this.tasks = tasks != null ? tasks : new ArrayList<>();
    }

    // Get all tasks
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    // Get tasks by status
    public List<Task> getTasksByStatus(String status) {
        return tasks.stream()
                .filter(task -> task.getStatus().equals(status))
                .collect(Collectors.toList());
    }

    // Get To-Do tasks
    public List<Task> getTodoTasks() {
        return getTasksByStatus(Task.STATUS_TODO);
    }

    // Get In-Progress tasks
    public List<Task> getInProgressTasks() {
        return getTasksByStatus(Task.STATUS_IN_PROGRESS);
    }

    // Get Done tasks
    public List<Task> getDoneTasks() {
        return getTasksByStatus(Task.STATUS_DONE);
    }

    // Add task
    public void addTask(Task task) {
        if (task != null && !tasks.contains(task)) {
            tasks.add(task);
        }
    }

    // Remove task
    public boolean removeTask(String taskId) {
        return tasks.removeIf(task -> task.getId().equals(taskId));
    }

    // Get task by ID
    public Task getTaskById(String taskId) {
        return tasks.stream()
                .filter(task -> task.getId().equals(taskId))
                .findFirst()
                .orElse(null);
    }

    // Move task to different status
    public boolean moveTask(String taskId, String newStatus) {
        Task task = getTaskById(taskId);
        if (task != null) {
            task.moveToStatus(newStatus);
            return true;
        }
        return false;
    }

    // Search tasks by title or description
    public List<Task> searchTasks(String searchTerm) {
        String lowerSearch = searchTerm.toLowerCase();
        return tasks.stream()
                .filter(task -> 
                    (task.getTitle() != null && task.getTitle().toLowerCase().contains(lowerSearch)) ||
                    (task.getDescription() != null && task.getDescription().toLowerCase().contains(lowerSearch))
                )
                .collect(Collectors.toList());
    }

    // Filter by priority
    public List<Task> filterByPriority(String priority) {
        return tasks.stream()
                .filter(task -> task.getPriority().equals(priority))
                .collect(Collectors.toList());
    }

    // Filter by tag
    public List<Task> filterByTag(String tag) {
        return tasks.stream()
                .filter(task -> task.getTags().contains(tag))
                .collect(Collectors.toList());
    }

    // Filter by assigned user
    public List<Task> filterByAssignedUser(String user) {
        return tasks.stream()
                .filter(task -> task.getAssignedTo() != null && task.getAssignedTo().equals(user))
                .collect(Collectors.toList());
    }

    // Get total time spent across all tasks
    public float getTotalTimeSpent() {
        return (float) tasks.stream()
                .mapToDouble(Task::getTimeSpent)
                .sum();
    }

    // Get total time spent for a specific status
    public float getTotalTimeSpentByStatus(String status) {
        return (float) tasks.stream()
                .filter(task -> task.getStatus().equals(status))
                .mapToDouble(Task::getTimeSpent)
                .sum();
    }

    // Get tasks count by status
    public int getTaskCountByStatus(String status) {
        return (int) tasks.stream()
                .filter(task -> task.getStatus().equals(status))
                .count();
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks != null ? tasks : new ArrayList<>();
    }
}

