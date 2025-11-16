package com.finalproject.service;

import com.finalproject.dao.TaskDAO;
import com.finalproject.model.Task;

import java.util.List;

/**
 * Service layer for Task operations - Uses MySQL database
 */
public class TaskService {
    private TaskDAO taskDAO;

    public TaskService() {
        this.taskDAO = new TaskDAO();
    }

    /**
     * Add a new task
     */
    public boolean addTask(String title, String description, String priority) {
        if (title == null || title.trim().isEmpty()) {
            System.out.println("Error: Task title is required!");
            return false;
        }

        Task task = new Task(title, description, priority);
        return taskDAO.insertTask(task);
    }

    /**
     * Get all tasks
     */
    public List<Task> getAllTasks() {
        return taskDAO.getAllTasks();
    }

    /**
     * Get task by ID
     */
    public Task getTaskById(String id) {
        if (id != null && !id.isEmpty()) {
            return taskDAO.getTaskById(id);
        }
        return null;
    }

    /**
     * Update task
     */
    public boolean updateTask(String taskId, String title, String description, String priority) {
        Task task = taskDAO.getTaskById(taskId);
        if (task == null) {
            System.out.println("Error: Task not found!");
            return false;
        }

        if (title != null && !title.trim().isEmpty()) {
            task.setTitle(title);
        }
        if (description != null) {
            task.setDescription(description);
        }
        if (priority != null) {
            task.setPriority(priority);
        }
        
        task.setUpdatedAt(java.time.LocalDateTime.now());
        return taskDAO.updateTask(task);
    }

    /**
     * Delete task
     */
    public boolean deleteTask(String taskId) {
        return taskDAO.deleteTask(taskId);
    }

    /**
     * Move task to different status
     */
    public boolean moveTask(String taskId, String newStatus) {
        if (!isValidStatus(newStatus)) {
            System.out.println("Error: Invalid status! Use: To-Do, In-Progress, or Done");
            return false;
        }
        
        Task task = taskDAO.getTaskById(taskId);
        if (task == null) {
            System.out.println("Error: Task not found!");
            return false;
        }
        
        task.setStatus(newStatus);
        task.setUpdatedAt(java.time.LocalDateTime.now());
        return taskDAO.updateTask(task);
    }

    /**
     * Get tasks by status
     */
    public List<Task> getTasksByStatus(String status) {
        if (isValidStatus(status)) {
            return taskDAO.getTasksByStatus(status);
        }
        return getAllTasks();
    }

    /**
     * Start task timer
     */
    public boolean startTimer(String taskId) {
        Task task = taskDAO.getTaskById(taskId);
        if (task == null) {
            System.out.println("Error: Task not found!");
            return false;
        }

        // Stop any other running timers first
        stopAllRunningTimers();

        task.startTimer();
        task.setUpdatedAt(java.time.LocalDateTime.now());
        return taskDAO.updateTask(task);
    }

    /**
     * Stop task timer
     */
    public boolean stopTimer(String taskId) {
        Task task = taskDAO.getTaskById(taskId);
        if (task == null) {
            System.out.println("Error: Task not found!");
            return false;
        }

        task.stopTimer();
        task.setUpdatedAt(java.time.LocalDateTime.now());
        return taskDAO.updateTask(task);
    }

    /**
     * Pause task timer
     */
    public boolean pauseTimer(String taskId) {
        Task task = taskDAO.getTaskById(taskId);
        if (task == null) {
            System.out.println("Error: Task not found!");
            return false;
        }

        task.pauseTimer();
        task.setUpdatedAt(java.time.LocalDateTime.now());
        return taskDAO.updateTask(task);
    }

    /**
     * Stop all running timers
     */
    private void stopAllRunningTimers() {
        getAllTasks().stream()
                .filter(Task::isTimerRunning)
                .forEach(task -> {
                    task.pauseTimer();
                    task.setUpdatedAt(java.time.LocalDateTime.now());
                    taskDAO.updateTask(task);
                });
    }

    /**
     * Search tasks
     */
    public List<Task> searchTasks(String searchTerm) {
        return taskDAO.searchTasks(searchTerm);
    }

    /**
     * Filter by priority
     */
    public List<Task> filterByPriority(String priority) {
        if (!isValidPriority(priority)) {
            System.out.println("Error: Invalid priority! Use: low, medium, or high");
            return List.of();
        }
        return taskDAO.filterByPriority(priority);
    }

    /**
     * Filter by tag
     */
    public List<Task> filterByTag(String tag) {
        return taskDAO.filterByTag(tag);
    }

    /**
     * Filter by assigned user
     */
    public List<Task> filterByAssignedUser(String user) {
        return taskDAO.filterByAssignedUser(user);
    }

    /**
     * Add tag to task
     */
    public boolean addTagToTask(String taskId, String tag) {
        Task task = taskDAO.getTaskById(taskId);
        if (task == null) {
            return false;
        }
        task.addTag(tag);
        task.setUpdatedAt(java.time.LocalDateTime.now());
        return taskDAO.updateTask(task);
    }

    /**
     * Assign task to user
     */
    public boolean assignTask(String taskId, String user) {
        Task task = taskDAO.getTaskById(taskId);
        if (task == null) {
            return false;
        }
        task.setAssignedTo(user);
        task.setUpdatedAt(java.time.LocalDateTime.now());
        return taskDAO.updateTask(task);
    }

    /**
     * Validate status
     */
    private boolean isValidStatus(String status) {
        return status != null && (
                status.equals(Task.STATUS_TODO) ||
                status.equals(Task.STATUS_IN_PROGRESS) ||
                status.equals(Task.STATUS_DONE)
        );
    }

    /**
     * Validate priority
     */
    private boolean isValidPriority(String priority) {
        return priority != null && (
                priority.equals(Task.PRIORITY_LOW) ||
                priority.equals(Task.PRIORITY_MEDIUM) ||
                priority.equals(Task.PRIORITY_HIGH)
        );
    }
}
