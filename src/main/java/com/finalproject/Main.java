package com.finalproject;

import com.finalproject.config.DatabaseConfig;
import com.finalproject.model.Task;
import com.finalproject.service.ImportExportService;
import com.finalproject.service.ReportService;
import com.finalproject.service.TaskService;
import com.finalproject.util.DatabaseSetup;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * TaskMate - Main Application
 * Kanban-style project management application with time tracking
 */
public class Main {
    private static Scanner scanner;
    private static TaskService taskService;
    private static ReportService reportService;
    private static ImportExportService importExportService;
    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        // Initialize scanner with explicit charset to avoid IDE issues
        try {
            scanner = new Scanner(System.in, "UTF-8");
        } catch (Exception e) {
            scanner = new Scanner(System.in);
        }
        
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║         TaskMate v1.0                 ║");
        System.out.println("║   Kanban Project Management System    ║");
        System.out.println("╚════════════════════════════════════════╝\n");
        
        // Flush output to ensure it's displayed
        System.out.flush();

        // Test database connection
        System.out.println("Connecting to database...");
        if (!DatabaseConfig.testConnection()) {
            System.out.println("ERROR: Cannot connect to database!");
            System.out.println("Please check your database configuration in:");
            System.out.println("src/main/resources/database.properties");
            System.out.println("\nMake sure:");
            System.out.println("1. MySQL is installed and running");
            System.out.println("2. Database 'taskmate_db' exists");
            System.out.println("3. Database credentials are correct");
            System.out.println("\nYou can create the database using:");
            System.out.println("Run: src/main/resources/database_schema.sql");
            return;
        }

        System.out.println("✓ Database connection successful!\n");

        // Check and create tables if they don't exist
        if (!DatabaseSetup.tablesExist()) {
            System.out.println("Creating database tables...");
            if (!DatabaseSetup.createTablesIfNotExist()) {
                System.out.println("ERROR: Failed to create database tables!");
                System.out.println("Please run the SQL script manually: src/main/resources/database_schema.sql");
                return;
            }
        }

        // Initialize services
        taskService = new TaskService();
        reportService = new ReportService(taskService);
        importExportService = new ImportExportService(taskService);
        
        int taskCount = taskService.getAllTasks().size();
        if (taskCount > 0) {
            System.out.println("✓ Loaded " + taskCount + " tasks from database.\n");
        } else {
            System.out.println("✓ Starting with empty board.\n");
        }

        // Main menu loop
        boolean running = true;
        while (running) {
            displayKanbanBoard();
            displayMenu();
            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    addTask();
                    break;
                case 2:
                    editTask();
                    break;
                case 3:
                    moveTask();
                    break;
                case 4:
                    deleteTask();
                    break;
                case 5:
                    startTimer();
                    break;
                case 6:
                    stopTimer();
                    break;
                case 7:
                    pauseTimer();
                    break;
                case 8:
                    assignTask();
                    break;
                case 9:
                    addTagToTask();
                    break;
                case 10:
                    searchTasks();
                    break;
                case 11:
                    filterTasks();
                    break;
                case 12:
                    generateReport();
                    break;
                case 13:
                    viewTaskDetails();
                    break;
                case 14:
                    exportToJson();
                    break;
                case 15:
                    importFromJson();
                    break;
                case 16:
                    exit();
                    running = false;
                    break;
                default:
                    System.out.println("⚠ Invalid choice! Please enter a number between 1 and 16.");
            }

            if (running) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }

        scanner.close();
    }

    /**
     * Display Kanban board with three columns
     */
    private static void displayKanbanBoard() {
        System.out.println("\n" + "═".repeat(100));
        System.out.println("KANBAN BOARD");
        System.out.println("═".repeat(100));

        // Get tasks from database and organize by status
        List<Task> allTasks = taskService.getAllTasks();
        List<Task> todoTasks = allTasks.stream()
                .filter(t -> t.getStatus().equals(Task.STATUS_TODO))
                .collect(java.util.stream.Collectors.toList());
        List<Task> inProgressTasks = allTasks.stream()
                .filter(t -> t.getStatus().equals(Task.STATUS_IN_PROGRESS))
                .collect(java.util.stream.Collectors.toList());
        List<Task> doneTasks = allTasks.stream()
                .filter(t -> t.getStatus().equals(Task.STATUS_DONE))
                .collect(java.util.stream.Collectors.toList());

        // Calculate max height for columns
        int maxHeight = Math.max(Math.max(todoTasks.size(), inProgressTasks.size()), doneTasks.size());

        System.out.println(String.format("%-33s│%-33s│%-33s", 
            " TO-DO (" + todoTasks.size() + ")", 
            " IN-PROGRESS (" + inProgressTasks.size() + ")", 
            " DONE (" + doneTasks.size() + ")"));

        System.out.println("─".repeat(100));

        // Display tasks in columns
        for (int i = 0; i < maxHeight; i++) {
            String todo = i < todoTasks.size() ? formatTaskForBoard(todoTasks.get(i)) : "";
            String inProgress = i < inProgressTasks.size() ? formatTaskForBoard(inProgressTasks.get(i)) : "";
            String done = i < doneTasks.size() ? formatTaskForBoard(doneTasks.get(i)) : "";

            System.out.println(String.format("%-33s│%-33s│%-33s", 
                truncate(todo, 33), 
                truncate(inProgress, 33), 
                truncate(done, 33)));
        }

        System.out.println("═".repeat(100));
    }

    /**
     * Format task for board display
     */
    private static String formatTaskForBoard(Task task) {
        String timerIndicator = task.isTimerRunning() ? "⏱ " : "";
        // Removed priority indicator to avoid display issues
        return String.format("%s[%s] %.2fh", 
            timerIndicator, 
            truncate(task.getTitle(), 20),
            task.getTimeSpent());
    }

    /**
     * Truncate string to max length
     */
    private static String truncate(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }

    /**
     * Display main menu
     */
    private static void displayMenu() {
        System.out.println("\n═══════════════════════════════════════════════════════════");
        System.out.println("MENU (Enter the NUMBER of your choice):");
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println(" 1.  Add New Task");
        System.out.println(" 2.  Edit Task");
        System.out.println(" 3.  Move Task (Change Status)");
        System.out.println(" 4.  Delete Task");
        System.out.println(" 5.  Start Timer");
        System.out.println(" 6.  Stop Timer");
        System.out.println(" 7.  Pause Timer");
        System.out.println(" 8.  Assign Task to User");
        System.out.println(" 9.  Add Tag to Task");
        System.out.println("10.  Search Tasks");
        System.out.println("11.  Filter Tasks");
        System.out.println("12.  Generate Report");
        System.out.println("13.  View Task Details");
        System.out.println("14.  Export to JSON File");
        System.out.println("15.  Import from JSON File");
        System.out.println("16.  Exit");
        System.out.println("═══════════════════════════════════════════════════════════");
    }

    /**
     * Add new task
     */
    private static void addTask() {
        System.out.println("\n--- Add New Task ---");
        String title = getStringInput("Task Title: ");
        String description = getStringInput("Description (optional): ");
        String priority = getPriorityInput("Priority (low/medium/high) [medium]: ");
        
        if (priority.isEmpty()) {
            priority = Task.PRIORITY_MEDIUM;
        }

        if (taskService.addTask(title, description, priority)) {
            System.out.println("✓ Task added successfully!");
        } else {
            System.out.println("✗ Failed to add task.");
        }
    }

    /**
     * Edit task
     */
    private static void editTask() {
        System.out.println("\n--- Edit Task ---");
        String taskId = getTaskIdInput();
        if (taskId == null) return;

        Task task = taskService.getTaskById(taskId);
        System.out.println("\nCurrent Task:");
        displayTaskDetails(task);

        String title = getStringInput("New Title (Enter to keep current): ");
        String description = getStringInput("New Description (Enter to keep current): ");
        String priority = getStringInput("New Priority (low/medium/high, Enter to keep current): ");

        if (taskService.updateTask(taskId, 
                title.isEmpty() ? null : title,
                description.isEmpty() ? null : description,
                priority.isEmpty() ? null : priority)) {
            System.out.println("✓ Task updated successfully!");
        } else {
            System.out.println("✗ Failed to update task.");
        }
    }

    /**
     * Move task to different status
     */
    private static void moveTask() {
        System.out.println("\n--- Move Task ---");
        String taskId = getTaskIdInput();
        if (taskId == null) return;

        Task task = taskService.getTaskById(taskId);
        System.out.println("\nCurrent Status: " + task.getStatus());
        System.out.println("\nAvailable Statuses:");
        System.out.println("1. To-Do");
        System.out.println("2. In-Progress");
        System.out.println("3. Done");

        int choice = getIntInput("Select new status (1-3): ");
        String newStatus = null;

        switch (choice) {
            case 1: newStatus = Task.STATUS_TODO; break;
            case 2: newStatus = Task.STATUS_IN_PROGRESS; break;
            case 3: newStatus = Task.STATUS_DONE; break;
            default:
                System.out.println("Invalid choice!");
                return;
        }

        if (taskService.moveTask(taskId, newStatus)) {
            System.out.println("✓ Task moved to " + newStatus + "!");
        } else {
            System.out.println("✗ Failed to move task.");
        }
    }

    /**
     * Delete task
     */
    private static void deleteTask() {
        System.out.println("\n--- Delete Task ---");
        String taskId = getTaskIdInput();
        if (taskId == null) return;

        Task task = taskService.getTaskById(taskId);
        System.out.println("\nTask to delete:");
        displayTaskDetails(task);

        String confirm = getStringInput("Are you sure? (yes/no): ");
        if ("yes".equalsIgnoreCase(confirm)) {
            if (taskService.deleteTask(taskId)) {
                System.out.println("✓ Task deleted successfully!");
            } else {
                System.out.println("✗ Failed to delete task.");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    /**
     * Start timer for a task
     */
    private static void startTimer() {
        System.out.println("\n--- Start Timer ---");
        String taskId = getTaskIdInput();
        if (taskId == null) return;

        if (taskService.startTimer(taskId)) {
            System.out.println("✓ Timer started for task!");
        } else {
            System.out.println("✗ Failed to start timer.");
        }
    }

    /**
     * Stop timer for a task
     */
    private static void stopTimer() {
        System.out.println("\n--- Stop Timer ---");
        String taskId = getTaskIdInput();
        if (taskId == null) return;

        if (taskService.stopTimer(taskId)) {
            Task task = taskService.getTaskById(taskId);
            System.out.println("✓ Timer stopped! Time spent: " + task.getFormattedTimeSpent());
        } else {
            System.out.println("✗ Failed to stop timer.");
        }
    }

    /**
     * Pause timer for a task
     */
    private static void pauseTimer() {
        System.out.println("\n--- Pause Timer ---");
        String taskId = getTaskIdInput();
        if (taskId == null) return;

        if (taskService.pauseTimer(taskId)) {
            Task task = taskService.getTaskById(taskId);
            System.out.println("✓ Timer paused! Total time: " + task.getFormattedTimeSpent());
        } else {
            System.out.println("✗ Failed to pause timer.");
        }
    }

    /**
     * Assign task to user
     */
    private static void assignTask() {
        System.out.println("\n--- Assign Task ---");
        String taskId = getTaskIdInput();
        if (taskId == null) return;

        String user = getStringInput("Assign to user: ");
        if (taskService.assignTask(taskId, user)) {
            System.out.println("✓ Task assigned to " + user + "!");
        } else {
            System.out.println("✗ Failed to assign task.");
        }
    }

    /**
     * Add tag to task
     */
    private static void addTagToTask() {
        System.out.println("\n--- Add Tag ---");
        String taskId = getTaskIdInput();
        if (taskId == null) return;

        String tag = getStringInput("Tag name: ");
        if (taskService.addTagToTask(taskId, tag)) {
            System.out.println("✓ Tag added!");
        } else {
            System.out.println("✗ Failed to add tag.");
        }
    }

    /**
     * Search tasks
     */
    private static void searchTasks() {
        System.out.println("\n--- Search Tasks ---");
        String searchTerm = getStringInput("Search term: ");

        List<Task> results = taskService.searchTasks(searchTerm);
        if (results.isEmpty()) {
            System.out.println("No tasks found.");
        } else {
            System.out.println("\nSearch Results (" + results.size() + "):");
            displayTaskList(results);
        }
    }

    /**
     * Filter tasks
     */
    private static void filterTasks() {
        System.out.println("\n--- Filter Tasks ---");
        System.out.println("1. By Priority");
        System.out.println("2. By Tag");
        System.out.println("3. By Assigned User");

        int choice = getIntInput("Select filter type (1-3): ");
        List<Task> results = null;

        switch (choice) {
            case 1:
                String priority = getPriorityInput("Priority (low/medium/high): ");
                results = taskService.filterByPriority(priority);
                break;
            case 2:
                String tag = getStringInput("Tag: ");
                results = taskService.filterByTag(tag);
                break;
            case 3:
                String user = getStringInput("User: ");
                results = taskService.filterByAssignedUser(user);
                break;
            default:
                System.out.println("Invalid choice!");
                return;
        }

        if (results == null || results.isEmpty()) {
            System.out.println("No tasks found.");
        } else {
            System.out.println("\nFiltered Results (" + results.size() + "):");
            displayTaskList(results);
        }
    }

    /**
     * Generate report
     */
    private static void generateReport() {
        System.out.println("\n--- Generate Report ---");
        System.out.println("1. Daily Report");
        System.out.println("2. Weekly Report");
        System.out.println("3. Overall Summary");

        int choice = getIntInput("Select report type (1-3): ");

        switch (choice) {
            case 1:
                LocalDate date = getDateInput("Enter date (yyyy-MM-dd) [today]: ");
                if (date == null) date = LocalDate.now();
                reportService.generateDailyReport(date);
                break;
            case 2:
                LocalDate startDate = getDateInput("Enter week start date (yyyy-MM-dd): ");
                if (startDate == null) startDate = LocalDate.now().minusDays(7);
                reportService.generateWeeklyReport(startDate);
                break;
            case 3:
                reportService.generateOverallReport();
                break;
            default:
                System.out.println("Invalid choice!");
        }
    }

    /**
     * View task details
     */
    private static void viewTaskDetails() {
        System.out.println("\n--- Task Details ---");
        String taskId = getTaskIdInput();
        if (taskId == null) return;

        Task task = taskService.getTaskById(taskId);
        if (task != null) {
            displayTaskDetails(task);
        } else {
            System.out.println("Task not found!");
        }
    }

    /**
     * Display task details
     */
    private static void displayTaskDetails(Task task) {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("Task ID: " + task.getId());
        System.out.println("Title: " + task.getTitle());
        System.out.println("Description: " + (task.getDescription() != null ? task.getDescription() : "N/A"));
        System.out.println("Status: " + task.getStatus());
        System.out.println("Priority: " + task.getPriority());
        System.out.println("Time Spent: " + task.getFormattedTimeSpent());
        System.out.println("Assigned To: " + (task.getAssignedTo() != null ? task.getAssignedTo() : "Unassigned"));
        System.out.println("Tags: " + (task.getTags().isEmpty() ? "None" : String.join(", ", task.getTags())));
        if (task.isTimerRunning()) {
            System.out.println("Timer: ⏱ RUNNING (started at " + task.getStartTime() + ")");
        }
        System.out.println("─".repeat(60));
    }

    /**
     * Display task list
     */
    private static void displayTaskList(List<Task> tasks) {
        System.out.println("\n" + "─".repeat(100));
        System.out.println(String.format("%-25s %-30s %-15s %-10s %-15s",
            "ID", "Title", "Status", "Priority", "Time Spent"));
        System.out.println("─".repeat(100));

        for (Task task : tasks) {
            // Show full ID or truncate if too long (max 25 chars)
            String displayId = task.getId().length() > 25 
                ? task.getId().substring(0, 22) + "..." 
                : task.getId();
            System.out.println(String.format("%-25s %-30s %-15s %-10s %-15s",
                displayId,
                truncate(task.getTitle(), 30),
                task.getStatus(),
                task.getPriority(),
                task.getFormattedTimeSpent()));
        }
        System.out.println("─".repeat(100));
    }

    /**
     * Get task ID from user
     */
    private static String getTaskIdInput() {
        List<Task> allTasks = taskService.getAllTasks();
        if (allTasks.isEmpty()) {
            System.out.println("No tasks available!");
            return null;
        }
        
        displayTaskList(allTasks);
        System.out.println("\nNote: Enter the FULL Task ID (shown above)");
        String taskId = getStringInput("Enter Task ID: ").trim();
        
        if (taskId.isEmpty()) {
            System.out.println("⚠ Task ID cannot be empty!");
            return null;
        }
        
        Task task = taskService.getTaskById(taskId);
        if (task == null) {
            // Try partial match as fallback
            List<Task> matches = allTasks.stream()
                .filter(t -> t.getId().startsWith(taskId) || t.getId().contains(taskId))
                .collect(java.util.stream.Collectors.toList());
            
            if (matches.size() == 1) {
                System.out.println("✓ Found task by partial match: " + matches.get(0).getId());
                return matches.get(0).getId();
            } else if (matches.size() > 1) {
                System.out.println("⚠ Multiple tasks found matching '" + taskId + "':");
                for (Task t : matches) {
                    System.out.println("  - " + t.getId() + " (" + t.getTitle() + ")");
                }
                System.out.println("Please enter the full Task ID.");
                return null;
            } else {
                System.out.println("✗ Task not found! Please check the ID and try again.");
                System.out.println("   Make sure to enter the FULL Task ID as shown in the list above.");
                return null;
            }
        }
        
        return taskId;
    }

    /**
     * Export tasks to JSON file
     */
    private static void exportToJson() {
        System.out.println("\n--- Export to JSON ---");
        String filePath = getStringInput("Enter file path (or press Enter for default): ").trim();
        
        if (filePath.isEmpty()) {
            if (importExportService.exportToJson()) {
                // Success message already shown in service
            }
        } else {
            if (importExportService.exportToJson(filePath)) {
                // Success message already shown in service
            }
        }
    }

    /**
     * Import tasks from JSON file
     */
    private static void importFromJson() {
        System.out.println("\n--- Import from JSON ---");
        String filePath = getStringInput("Enter file path (or press Enter for default 'taskmate_data.json'): ").trim();
        
        if (filePath.isEmpty()) {
            if (importExportService.importFromJson()) {
                // Success message already shown in service
            }
        } else {
            if (importExportService.importFromJson(filePath)) {
                // Success message already shown in service
            }
        }
    }

    /**
     * Exit application
     */
    private static void exit() {
        System.out.println("\nThank you for using TaskMate!");
        System.out.println("All data is saved in the database.");
    }

    // Helper methods for input
    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            System.out.flush(); // Ensure prompt is displayed
            
            try {
                if (!scanner.hasNextLine()) {
                    // If scanner is closed or no input available, wait a bit
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    continue;
                }
                
                String input = scanner.nextLine().trim();
                
                // Debug output (can be removed later)
                if (input.startsWith("{") || input.contains("_request")) {
                    System.out.println("⚠ Detected debugger input. Please run in Terminal mode, not Debug Console.");
                    System.out.println("   In Cursor: Use 'Run' button, not 'Debug' button");
                    continue;
                }
                
                if (input.isEmpty()) {
                    System.out.println("⚠ Please enter a number (1-16)!");
                    continue;
                }
                
                int choice = Integer.parseInt(input);
                if (choice < 1 || choice > 16) {
                    System.out.println("⚠ Invalid choice! Please enter a number between 1 and 16.");
                    continue;
                }
                return choice;
            } catch (NumberFormatException e) {
                System.out.println("⚠ Invalid input! Please enter a NUMBER (1-16), not text.");
                System.out.println("   Example: Type '1' to Add New Task, not 'Add New Task'");
            } catch (Exception e) {
                System.out.println("⚠ Error reading input: " + e.getMessage());
                System.out.println("   Try running in Terminal instead of Debug Console");
            }
        }
    }

    private static String getPriorityInput(String prompt) {
        while (true) {
            String input = getStringInput(prompt);
            if (input.isEmpty()) return input;
            String lower = input.toLowerCase();
            if (lower.equals("low") || lower.equals("medium") || lower.equals("high")) {
                return lower;
            }
            System.out.println("Invalid priority! Use: low, medium, or high");
        }
    }

    private static LocalDate getDateInput(String prompt) {
        while (true) {
            String input = getStringInput(prompt);
            if (input.isEmpty()) return null;
            try {
                return LocalDate.parse(input, dateFormatter);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format! Use yyyy-MM-dd");
            }
        }
    }
}
