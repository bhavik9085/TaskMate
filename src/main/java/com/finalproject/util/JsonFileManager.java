package com.finalproject.util;

import com.finalproject.model.KanbanBoard;
import com.finalproject.model.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages saving and loading Kanban board data from JSON file
 */
public class JsonFileManager {
    private static final String DATA_FILE = "taskmate_data.json";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // Custom TypeAdapter for LocalDateTime
    private static final TypeAdapter<LocalDateTime> localDateTimeAdapter = new TypeAdapter<LocalDateTime>() {
        @Override
        public void write(JsonWriter out, LocalDateTime value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.format(formatter));
            }
        }

        @Override
        public LocalDateTime read(JsonReader in) throws IOException {
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

    /**
     * Save Kanban board to JSON file
     */
    public static boolean saveBoard(KanbanBoard board) {
        try {
            String json = gson.toJson(board.getAllTasks());
            Files.write(Paths.get(DATA_FILE), json.getBytes());
            return true;
        } catch (IOException e) {
            System.err.println("Error saving board data: " + e.getMessage());
            return false;
        }
    }

    /**
     * Load Kanban board from JSON file
     */
    public static KanbanBoard loadBoard() {
        try {
            File file = new File(DATA_FILE);
            if (!file.exists()) {
                // Return empty board if file doesn't exist
                return new KanbanBoard();
            }

            String json = new String(Files.readAllBytes(Paths.get(DATA_FILE)));
            
            if (json == null || json.trim().isEmpty()) {
                return new KanbanBoard();
            }

            Type listType = new TypeToken<ArrayList<Task>>(){}.getType();
            List<Task> tasks = gson.fromJson(json, listType);
            
            return new KanbanBoard(tasks != null ? tasks : new ArrayList<>());
        } catch (IOException e) {
            System.err.println("Error loading board data: " + e.getMessage());
            return new KanbanBoard();
        }
    }

    /**
     * Check if data file exists
     */
    public static boolean dataFileExists() {
        return new File(DATA_FILE).exists();
    }
}

