package com.finalproject.service;

import com.finalproject.model.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for importing and exporting tasks to/from JSON files
 */
public class ImportExportService {
    private TaskService taskService;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // Custom TypeAdapter for LocalDateTime (same as JsonFileManager)
    private static final com.google.gson.TypeAdapter<LocalDateTime> localDateTimeAdapter = 
        new com.google.gson.TypeAdapter<LocalDateTime>() {
            @Override
            public void write(com.google.gson.stream.JsonWriter out, LocalDateTime value) throws IOException {
                if (value == null) {
                    out.nullValue();
                } else {
                    out.value(value.format(formatter));
                }
            }

            @Override
            public LocalDateTime read(com.google.gson.stream.JsonReader in) throws IOException {
                if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                    in.nextNull();
                    return null;
                }
                String dateTimeStr = in.nextString();
                if (dateTimeStr == null || dateTimeStr.isEmpty()) {
                    return null;
                }
                return LocalDateTime.parse(dateTimeStr, formatter);
            }
        };
    
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, localDateTimeAdapter)
            .create();

    public ImportExportService(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Export all tasks to JSON file
     */
    public boolean exportToJson(String filePath) {
        try {
            List<Task> allTasks = taskService.getAllTasks();
            
            if (allTasks.isEmpty()) {
                System.out.println("No tasks to export!");
                return false;
            }
            
            String json = gson.toJson(allTasks);
            Files.write(Paths.get(filePath), json.getBytes());
            
            System.out.println("✓ Successfully exported " + allTasks.size() + " tasks to: " + filePath);
            return true;
        } catch (IOException e) {
            System.err.println("Error exporting to JSON: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Import tasks from JSON file
     */
    public boolean importFromJson(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("✗ File not found: " + filePath);
                return false;
            }

            String json = new String(Files.readAllBytes(Paths.get(filePath)));
            
            if (json == null || json.trim().isEmpty()) {
                System.out.println("✗ File is empty!");
                return false;
            }

            com.google.gson.reflect.TypeToken<ArrayList<Task>> listType = new TypeToken<ArrayList<Task>>(){};
            List<Task> tasks = gson.fromJson(json, listType.getType());
            
            if (tasks == null || tasks.isEmpty()) {
                System.out.println("✗ No tasks found in file!");
                return false;
            }
            
            int imported = 0;
            int skipped = 0;
            
            for (Task task : tasks) {
                // Check if task already exists
                Task existing = taskService.getTaskById(task.getId());
                if (existing != null) {
                    System.out.println("⚠ Task " + task.getId() + " already exists. Skipping...");
                    skipped++;
                    continue;
                }
                
                // Validate task before importing
                if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
                    System.out.println("⚠ Invalid task found (missing title). Skipping...");
                    skipped++;
                    continue;
                }
                
                // Ensure required fields have defaults
                if (task.getStatus() == null) {
                    task.setStatus(Task.STATUS_TODO);
                }
                if (task.getPriority() == null) {
                    task.setPriority(Task.PRIORITY_MEDIUM);
                }
                if (task.getCreatedAt() == null) {
                    task.setCreatedAt(LocalDateTime.now());
                }
                if (task.getUpdatedAt() == null) {
                    task.setUpdatedAt(LocalDateTime.now());
                }
                
                // Import task to database using TaskDAO
                com.finalproject.dao.TaskDAO taskDAO = new com.finalproject.dao.TaskDAO();
                if (taskDAO.insertTask(task)) {
                    imported++;
                } else {
                    skipped++;
                }
            }
            
            System.out.println("✓ Import completed!");
            System.out.println("  Imported: " + imported + " tasks");
            System.out.println("  Skipped: " + skipped + " tasks");
            
            return imported > 0;
        } catch (IOException e) {
            System.err.println("Error importing from JSON: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Export with default filename (timestamped)
     */
    public boolean exportToJson() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String defaultPath = "taskmate_export_" + timestamp + ".json";
        return exportToJson(defaultPath);
    }

    /**
     * Import from default file
     */
    public boolean importFromJson() {
        return importFromJson("taskmate_data.json");
    }
}

