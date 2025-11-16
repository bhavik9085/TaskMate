# TaskMate - Kanban Project Management System

**Name:** Bhavik Parmar  
**Neptun Code:** GM186Q

## 📋 Project Overview

TaskMate is a lightweight project management application built for programmers to organize tasks efficiently using a Kanban-style board system. It focuses on visualizing workflow stages — "To-Do", "In-Progress", and "Done" — while integrating a built-in time tracking feature that records how long each task takes to complete.

### Key Features

- ✅ **Kanban Board** - Visualize tasks in three columns (To-Do, In-Progress, Done)
- ⏱️ **Time Tracking** - Start, pause, and stop timers for individual tasks
- 💾 **MySQL Database** - Persistent storage with automatic table creation
- 📤 **JSON Import/Export** - Import tasks from JSON or export to JSON for backup
- 📊 **Reports** - Generate daily/weekly time summary reports
- 🔍 **Search & Filter** - Search tasks by title/description or filter by priority, tag, or assigned user
- 👥 **Task Assignment** - Assign tasks to team members
- 🏷️ **Tags** - Categorize tasks with tags

## 🎯 Use Cases

- Store and retrieve task data from MySQL database
- Import/export tasks to/from JSON files for backup and migration
- Add, edit, move, and delete task cards across phases
- Start, pause, and stop an individual task timer
- Generate daily/weekly time summary reports
- Search or filter tasks by priority, tag, or assigned user
- Sync project progresses through a REST API or local network backup (future)

## 📁 Project Structure

```
final_project/
├── pom.xml                          # Maven configuration
├── README.md                        # This file
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── finalproject/
│   │   │           ├── Main.java                    # Main application
│   │   │           ├── config/
│   │   │           │   └── DatabaseConfig.java      # Database configuration
│   │   │           ├── dao/
│   │   │           │   └── TaskDAO.java             # Data Access Object
│   │   │           ├── model/
│   │   │           │   ├── Task.java                # Task entity
│   │   │           │   └── KanbanBoard.java         # Board model
│   │   │           ├── service/
│   │   │           │   ├── TaskService.java         # Task operations
│   │   │           │   ├── ReportService.java       # Report generation
│   │   │           │   └── ImportExportService.java # JSON import/export
│   │   │           └── util/
│   │   │               ├── DatabaseUtil.java        # DB utility methods
│   │   │               ├── DatabaseSetup.java       # Auto table creation
│   │   │               └── IdGenerator.java         # Task ID generation
│   │   └── resources/
│   │       ├── database.properties                  # DB connection config
│   │       └── database_schema.sql                 # Database schema
│   └── test/
```

## 🚀 Quick Start

### Prerequisites

1. **Java JDK 11+**
   - Verify: `java -version`

2. **MySQL Server 8.0+**
   - Install MySQL from [mysql.com](https://dev.mysql.com/downloads/mysql/)
   - Verify: `mysql --version`
   - Make sure MySQL service is running

3. **Maven** (Optional - Cursor handles it automatically)
   - Verify: `mvn -version`

### Running in Cursor IDE

1. **Install Java Extension Pack** in Cursor:
   - Press `Ctrl+Shift+X` → Search "Extension Pack for Java" → Install

2. **Set up MySQL Database:**
   - Create database: `CREATE DATABASE taskmate_db;`
   - Or run the schema script: `mysql -u root -p < src/main/resources/database_schema.sql`
   - Configure connection in `src/main/resources/database.properties`

3. **Open the project folder** in Cursor

4. **Wait for dependencies to load** (check bottom right corner)

5. **Run the application:**
   - Open `Main.java`
   - Click the **▶ Run** button or press `F5`
   - Tables will be created automatically if they don't exist

### Running from Command Line

```bash
# Build project
mvn clean compile

# Run application
mvn exec:java -Dexec.mainClass="com.finalproject.Main"
```

## 📊 Data Model

### Task Structure

```java
Task {
    String id              // Unique task ID (T1, T2, T3, etc.)
    String title           // Task name
    String description     // Task details
    String status          // "To-Do" / "In-Progress" / "Done"
    String priority        // "low" / "medium" / "high"
    float timeSpent        // Total hours logged
    String startTime       // When timer started
    String endTime         // When timer stopped
    List<String> tags      // Optional categories
    String assignedTo      // Team member (optional)
    LocalDateTime createdAt    // Creation timestamp
    LocalDateTime updatedAt    // Last update timestamp
}
```

## 🎮 User Interface

### Kanban Board Display

The application displays a visual Kanban board with three columns:

```
═══════════════════════════════════════════════════════════════════════════════
KANBAN BOARD
═══════════════════════════════════════════════════════════════════════════════
 TO-DO (2)                    │ IN-PROGRESS (1)              │ DONE (3)
───────────────────────────────────────────────────────────────────────────────
🔴 Task 1 [high] 0.00h       │ ⏱ 🟡 Task 3 [medium] 2.50h  │ 🟢 Task 2 [low] 5.00h
🟡 Task 4 [medium] 0.00h      │                              │
═══════════════════════════════════════════════════════════════════════════════
```

### Menu Options

1. **Add New Task** - Create a new task card
2. **Edit Task** - Modify task details
3. **Move Task** - Change task status (To-Do ↔ In-Progress ↔ Done)
4. **Delete Task** - Remove a task
5. **Start Timer** - Start tracking time for a task
6. **Stop Timer** - Stop timer and log time spent
7. **Pause Timer** - Pause timer without stopping
8. **Assign Task** - Assign task to a team member
9. **Add Tag** - Add category tag to task
10. **Search Tasks** - Search by title/description
11. **Filter Tasks** - Filter by priority, tag, or user
12. **Generate Report** - Create time summary reports
13. **View Task Details** - See full task information
14. **Export to JSON File** - Export all tasks to JSON for backup
15. **Import from JSON File** - Import tasks from JSON file
16. **Exit** - Exit application (data saved automatically)

## 💾 Data Persistence

### MySQL Database Storage (Primary)

TaskMate uses **MySQL** as its primary data storage:
- **Automatic Table Creation** - Tables are created automatically on first run
- **Real-time Persistence** - All changes are saved immediately to the database
- **Relational Structure** - Tasks and tags stored in normalized tables
- **Simple Task IDs** - Sequential IDs (T1, T2, T3, etc.) for easy reference

**Database Schema:**
- `tasks` table - Stores all task information
- `task_tags` table - Junction table for task-tag relationships

### JSON Import/Export (Backup & Migration)

Import and export functionality available:
- **Export to JSON** - Backup all tasks to a JSON file (Menu option 14)
- **Import from JSON** - Restore tasks from a JSON file (Menu option 15)
- **Portable** - Easy to backup, share, or migrate data
- **Format** - Standard JSON format compatible with other tools

## 📈 Reports

### Daily Report
Shows time spent on tasks for a specific date:
- Total time spent
- Tasks breakdown
- Time by status

### Weekly Report
Shows time spent for a week:
- Total time and daily average
- Daily breakdown
- Tasks completed

### Overall Summary
Shows complete statistics:
- Total tasks and time
- Tasks by status
- Time by status

## 🔍 Search & Filter

- **Search:** Find tasks by title or description
- **Filter by Priority:** low, medium, or high
- **Filter by Tag:** Find tasks with specific tags
- **Filter by User:** Find tasks assigned to a user

## ⏱️ Time Tracking

- **Start Timer:** Begins tracking time for a task (moves to In-Progress)
- **Stop Timer:** Stops timer and adds time to total
- **Pause Timer:** Pauses timer without moving task
- Only one timer can run at a time

## 🎯 User Story Example

> When Bhavik opens TaskMate, the application connects to the MySQL database and loads all saved tasks automatically. He checks the "To-Do" column and moves the "Implement login system" task (T1) to "In-Progress", starting a timer to track time worked. Once complete, he moves the task to "Done", stopping the timer and logging total time spent. Then he creates a new "Fix dashboard alignment" task (T2), assigns a high priority, and adds tags. Before exiting, TaskMate generates a summary report displaying hours worked across tasks for the day. All changes are automatically saved to the database. He can also export all tasks to JSON for backup using the export feature.

## 🛠️ Technologies Used

- **Java 11+** - Programming language
- **MySQL 8.0+** - Relational database for data persistence
- **JDBC** - Java Database Connectivity for MySQL operations
- **Gson** - JSON serialization/deserialization for import/export
- **Maven** - Dependency management and build automation

## 📝 Configuration

### Database Setup (Required)

1. **Create MySQL Database:**
   ```sql
   CREATE DATABASE taskmate_db;
   ```

2. **Configure Connection:**
   Edit `src/main/resources/database.properties`:
   ```properties
   db.driver=com.mysql.cj.jdbc.Driver
   db.url=jdbc:mysql://localhost:3306/taskmate_db
   db.username=root
   db.password=your_password
   ```

3. **Automatic Table Creation:**
   - Tables are created automatically on first run
   - Or run manually: `mysql -u root -p < src/main/resources/database_schema.sql`

### JSON Import/Export

- **Default Export File:** `taskmate_export.json` (in project root)
- **Custom Path:** Specify file path when prompted during export/import

## 🐛 Troubleshooting

### Issue: "Cannot connect to database"
**Solution:** 
- Ensure MySQL server is running: `mysql --version`
- Check database credentials in `database.properties`
- Verify database exists: `CREATE DATABASE taskmate_db;`
- Check MySQL service status

### Issue: "Table 'taskmate_db.tasks' doesn't exist"
**Solution:** 
- Tables are created automatically on first run
- Or run manually: `mysql -u root -p < src/main/resources/database_schema.sql`
- Check `DatabaseSetup.java` logs for errors

### Issue: "Timer not working"
**Solution:** Make sure only one timer is running at a time. Start timer moves task to In-Progress.

### Issue: "Task not found" when entering Task ID
**Solution:** Use the simple task ID format (T1, T2, T3, etc.) shown in the Kanban board display.

### Issue: "Import/Export not working"
**Solution:** 
- Check file permissions for JSON file
- Ensure JSON file format is valid
- Verify file path is correct

## 📚 Documentation

- `PROJECT_OVERVIEW.md` - Detailed project explanation
- `SETUP_GUIDE.md` - Step-by-step setup instructions
- `CURSOR_RUN_GUIDE.md` - Running in Cursor IDE
- `RUN_WITHOUT_MAVEN.md` - Alternative setup methods

## 🔮 Future Enhancements

- REST API for syncing across devices
- Local network backup
- GUI interface (Swing/JavaFX)
- Export reports to PDF
- Task dependencies
- Due dates and reminders
- Team collaboration features

## 📄 License

This project is created for educational purposes as part of Programming 3 final project.

## 👤 Author

**Bhavik Parmar**  
Neptun Code: GM186Q

---

**TaskMate** - Organize. Track. Complete. 🚀
