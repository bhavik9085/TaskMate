package com.finalproject.service;

import com.finalproject.model.Task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for generating time summary reports
 */
public class ReportService {
    private TaskService taskService;
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ReportService(TaskService taskService) {
        this.taskService = taskService;
    }
    
    // Constructor for backward compatibility with KanbanBoard
    public ReportService(com.finalproject.model.KanbanBoard board) {
        // Create a temporary TaskService - this is for compatibility only
        this.taskService = new TaskService();
    }

    /**
     * Generate daily time summary report
     */
    public void generateDailyReport(LocalDate date) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("DAILY TIME SUMMARY REPORT");
        System.out.println("Date: " + date.format(dateFormatter));
        System.out.println("=".repeat(60));

        List<Task> tasksForDay = getTasksForDate(date);
        
        if (tasksForDay.isEmpty()) {
            System.out.println("No tasks found for this date.");
            return;
        }

        float totalTime = (float) tasksForDay.stream()
                .mapToDouble(Task::getTimeSpent)
                .sum();

        System.out.println("\nTotal Time Spent: " + String.format("%.2f", totalTime) + " hours");
        System.out.println("\nTasks Breakdown:");
        System.out.println("-".repeat(60));

        for (Task task : tasksForDay) {
            System.out.println(String.format("• %s [%s] - %.2fh", 
                task.getTitle(), 
                task.getStatus(), 
                task.getTimeSpent()));
        }

        // Status breakdown
        Map<String, Float> statusTime = tasksForDay.stream()
                .collect(Collectors.groupingBy(
                    Task::getStatus,
                    Collectors.collectingAndThen(
                        Collectors.summingDouble(Task::getTimeSpent),
                        Double::floatValue
                    )
                ));

        System.out.println("\nTime by Status:");
        statusTime.forEach((status, time) -> 
            System.out.println(String.format("  %s: %.2fh", status, time))
        );

        System.out.println("=".repeat(60) + "\n");
    }

    /**
     * Generate weekly time summary report
     */
    public void generateWeeklyReport(LocalDate startDate) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("WEEKLY TIME SUMMARY REPORT");
        System.out.println("Week Starting: " + startDate.format(dateFormatter));
        System.out.println("=".repeat(60));

        LocalDate endDate = startDate.plusDays(6);
        List<Task> tasksForWeek = getTasksForDateRange(startDate, endDate);

        if (tasksForWeek.isEmpty()) {
            System.out.println("No tasks found for this week.");
            return;
        }

        float totalTime = (float) tasksForWeek.stream()
                .mapToDouble(Task::getTimeSpent)
                .sum();

        System.out.println("\nTotal Time Spent: " + String.format("%.2f", totalTime) + " hours");
        System.out.println("Average per Day: " + String.format("%.2f", totalTime / 7) + " hours");

        // Daily breakdown
        Map<LocalDate, Float> dailyTime = tasksForWeek.stream()
                .collect(Collectors.groupingBy(
                    task -> getTaskDate(task),
                    Collectors.collectingAndThen(
                        Collectors.summingDouble(Task::getTimeSpent),
                        Double::floatValue
                    )
                ));

        System.out.println("\nDaily Breakdown:");
        System.out.println("-".repeat(60));
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            float dayTime = dailyTime.getOrDefault(date, 0.0f);
            System.out.println(String.format("  %s: %.2fh", 
                date.format(dateFormatter), 
                dayTime));
        }

        // Task summary
        System.out.println("\nTasks Completed:");
        long doneCount = tasksForWeek.stream()
                .filter(task -> task.getStatus().equals(Task.STATUS_DONE))
                .count();
        System.out.println("  Done: " + doneCount + " tasks");

        System.out.println("=".repeat(60) + "\n");
    }

    /**
     * Generate overall summary report
     */
    public void generateOverallReport() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("OVERALL SUMMARY REPORT");
        System.out.println("=".repeat(60));

        List<Task> allTasks = taskService.getAllTasks();
        float totalTime = (float) allTasks.stream().mapToDouble(Task::getTimeSpent).sum();
        int totalTasks = allTasks.size();

        System.out.println("\nTotal Tasks: " + totalTasks);
        System.out.println("Total Time Spent: " + String.format("%.2f", totalTime) + " hours");

        Map<String, Long> statusCounts = allTasks.stream()
                .collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()));
        
        Map<String, Float> statusTime = allTasks.stream()
                .collect(Collectors.groupingBy(
                    Task::getStatus,
                    Collectors.collectingAndThen(
                        Collectors.summingDouble(Task::getTimeSpent),
                        Double::floatValue
                    )
                ));

        System.out.println("\nTasks by Status:");
        System.out.println("  To-Do: " + statusCounts.getOrDefault(Task.STATUS_TODO, 0L));
        System.out.println("  In-Progress: " + statusCounts.getOrDefault(Task.STATUS_IN_PROGRESS, 0L));
        System.out.println("  Done: " + statusCounts.getOrDefault(Task.STATUS_DONE, 0L));

        System.out.println("\nTime by Status:");
        System.out.println("  To-Do: " + String.format("%.2f", 
            statusTime.getOrDefault(Task.STATUS_TODO, 0.0f)) + "h");
        System.out.println("  In-Progress: " + String.format("%.2f", 
            statusTime.getOrDefault(Task.STATUS_IN_PROGRESS, 0.0f)) + "h");
        System.out.println("  Done: " + String.format("%.2f", 
            statusTime.getOrDefault(Task.STATUS_DONE, 0.0f)) + "h");

        System.out.println("=".repeat(60) + "\n");
    }

    /**
     * Get tasks for a specific date (simplified - checks if task was updated on that date)
     */
    private List<Task> getTasksForDate(LocalDate date) {
        return taskService.getAllTasks().stream()
                .filter(task -> {
                    LocalDateTime updated = task.getUpdatedAt();
                    return updated != null && updated.toLocalDate().equals(date);
                })
                .collect(Collectors.toList());
    }

    /**
     * Get tasks for a date range
     */
    private List<Task> getTasksForDateRange(LocalDate startDate, LocalDate endDate) {
        return taskService.getAllTasks().stream()
                .filter(task -> {
                    LocalDateTime updated = task.getUpdatedAt();
                    if (updated == null) return false;
                    LocalDate taskDate = updated.toLocalDate();
                    return !taskDate.isBefore(startDate) && !taskDate.isAfter(endDate);
                })
                .collect(Collectors.toList());
    }

    /**
     * Get date from task (uses updated date as proxy)
     */
    private LocalDate getTaskDate(Task task) {
        LocalDateTime updated = task.getUpdatedAt();
        return updated != null ? updated.toLocalDate() : LocalDate.now();
    }
}

