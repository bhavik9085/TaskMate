# TaskMate - Project Documentation

**Name:** Bhavik Parmar  
**Neptun Code:** GM186Q

---

## Table of Contents

1. [Application Overview](#application-overview)
2. [Used Modules and Libraries](#used-modules-and-libraries)
3. [Use Cases](#use-cases)
   - [Add New Task](#1-add-new-task)
   - [Edit Task](#2-edit-task)
   - [Move Task](#3-move-task-change-status)
   - [Delete Task](#4-delete-task)
   - [Start Timer](#5-start-timer)
   - [Stop Timer](#6-stop-timer)
   - [Pause Timer](#7-pause-timer)
   - [Assign Task to User](#8-assign-task-to-user)
   - [Add Tag to Task](#9-add-tag-to-task)
   - [Search Tasks](#10-search-tasks)
   - [Filter Tasks](#11-filter-tasks)
   - [Generate Report](#12-generate-report)
   - [View Task Details](#13-view-task-details)
   - [Export to JSON File](#14-export-to-json-file)
   - [Import from JSON File](#15-import-from-json-file)
4. [Project Summary](#project-summary)
5. [Course Summary](#course-summary)

---

## Application Overview

**TaskMate** is a lightweight, console-based project management application designed for programmers and teams to organize tasks efficiently using a Kanban-style board system. The application provides a visual representation of workflow stages through three columns: "To-Do", "In-Progress", and "Done". TaskMate integrates built-in time tracking capabilities that record how long each task takes to complete, making it an ideal tool for project management and productivity tracking.

The application uses MySQL as its primary database for persistent storage, ensuring all task data is automatically saved and retrieved. Additionally, TaskMate supports JSON import/export functionality for backup and data migration purposes. The system includes advanced search and filtering capabilities, comprehensive reporting features, and task organization tools such as priority levels, tags, and user assignment.

---

## Used Modules and Libraries

The following table lists all the modules and libraries used in the TaskMate project:

| Module/Library | Version | Purpose |
|----------------|---------|---------|
| **MySQL Connector/J** | 8.0.33 | JDBC driver for MySQL database connectivity |
| **H2 Database** | 2.2.224 | In-memory database for testing and alternative storage |
| **SLF4J API** | 1.7.36 | Simple Logging Facade for Java - logging abstraction |
| **SLF4J Simple** | 1.7.36 | Simple implementation of SLF4J for logging |
| **Gson** | 2.10.1 | Google's JSON library for serialization/deserialization |
| **JUnit** | 4.13.2 | Unit testing framework for Java |
| **Maven Compiler Plugin** | 3.11.0 | Compiles Java source code |
| **Maven Exec Plugin** | 3.1.0 | Executes Java applications |

**Java Version:** JDK 11+  
**Build Tool:** Apache Maven  
**Database:** MySQL 8.0+

---

## Use Cases

### 1. Add New Task

**Screenshot Before:**

<img width="1626" height="959" alt="image" src="https://github.com/user-attachments/assets/65a51fea-40bb-42a2-b533-3cf046fec7a3" />


**Screenshot After:**

<img width="1664" height="1343" alt="image" src="https://github.com/user-attachments/assets/945630dc-13e3-4711-ac8d-d50174e932b0" />

**Explanation:**
Users can create new tasks by selecting option 1 from the main menu. The system prompts for task title, description (optional), and priority level (low/medium/high). Upon successful creation, the task is automatically assigned a unique sequential ID (T1, T2, T3, etc.) and appears in the "To-Do" column of the Kanban board. The task is immediately saved to the MySQL database for persistence.

---

### 2. Edit Task

**Screenshot Before:**
```
[Placeholder: Screenshot showing task details before editing]
```

**Screenshot After:**
```
[Placeholder: Screenshot showing updated task details after editing]
```

**Explanation:**
The edit functionality allows users to modify existing task properties including title, description, and priority. Users select option 2, enter the task ID, and can update any field by entering new values or pressing Enter to keep current values. Changes are immediately reflected in the database and displayed on the Kanban board.

---

### 3. Move Task (Change Status)

**Screenshot Before:**
```
[Placeholder: Screenshot showing task in "To-Do" column]
```

**Screenshot After:**
```
[Placeholder: Screenshot showing the same task moved to "In-Progress" column]
```

**Explanation:**
Tasks can be moved between the three workflow stages: To-Do, In-Progress, and Done. Users select option 3, choose the task ID, and select the new status. This feature enables visual workflow management, allowing users to track task progress through different stages of completion.

---

### 4. Delete Task

**Screenshot Before:**
```
[Placeholder: Screenshot showing Kanban board with task present]
```

**Screenshot After:**
```
[Placeholder: Screenshot showing Kanban board after task deletion]
```

**Explanation:**
The delete functionality removes tasks permanently from the system. Users select option 4, enter the task ID, and confirm deletion. The system removes the task from the database and all associated tags, ensuring data consistency. A confirmation prompt prevents accidental deletions.

---

### 5. Start Timer

**Screenshot Before:**
```
[Placeholder: Screenshot showing task without timer running]
```

**Screenshot After:**
```
[Placeholder: Screenshot showing task with timer indicator (⏱) and moved to In-Progress]
```

**Explanation:**
Starting a timer begins time tracking for a selected task. When a timer is started, the task automatically moves to "In-Progress" status if it was in "To-Do". The system records the start time and displays a timer indicator (⏱) next to the task on the Kanban board. Only one timer can run at a time.

---

### 6. Stop Timer

**Screenshot Before:**
```
[Placeholder: Screenshot showing task with timer running]
```

**Screenshot After:**
```
[Placeholder: Screenshot showing task with timer stopped and accumulated time displayed]
```

**Explanation:**
Stopping the timer ends time tracking and calculates the total time spent. The elapsed time is added to the task's total time spent counter, and the timer indicator is removed. The task remains in its current status, allowing users to continue working or move it to "Done" when complete.

---

### 7. Pause Timer

**Screenshot Before:**
```
[Placeholder: Screenshot showing timer running]
```

**Screenshot After:**
```
[Placeholder: Screenshot showing timer paused with accumulated time preserved]
```

**Explanation:**
Pausing a timer temporarily stops time tracking without ending the session. The accumulated time up to the pause point is saved, and the timer can be resumed later. This feature is useful for taking breaks while maintaining accurate time tracking without losing progress.

---

### 8. Assign Task to User

**Screenshot Before:**
```
[Placeholder: Screenshot showing task with "Unassigned" status]
```

**Screenshot After:**
```
[Placeholder: Screenshot showing task assigned to a specific user]
```

**Explanation:**
Tasks can be assigned to team members for better collaboration and accountability. Users select option 8, enter the task ID, and specify the assignee's name. The assigned user information is stored in the database and displayed when viewing task details, enabling team members to identify their responsibilities.

---

### 9. Add Tag to Task

**Screenshot Before:**
```
[Placeholder: Screenshot showing task without tags]
```

**Screenshot After:**
```
[Placeholder: Screenshot showing task with added tags displayed]
```

**Explanation:**
Tags provide a flexible categorization system for tasks. Users can add multiple tags to a single task, enabling filtering and organization by project, feature, bug type, or any custom category. Tags are stored in a separate junction table in the database, maintaining a many-to-many relationship between tasks and tags.

---

### 10. Search Tasks

**Screenshot Before:**
```
[Placeholder: Screenshot showing search prompt]
```

**Screenshot After:**
```
[Placeholder: Screenshot showing search results matching the query]
```

**Explanation:**
The search functionality allows users to find tasks by entering keywords that match task titles or descriptions. The system performs a case-insensitive search across all tasks and displays matching results in a formatted list. This feature helps users quickly locate specific tasks in large project boards.

---

### 11. Filter Tasks

**Screenshot Before:**
```
[Placeholder: Screenshot showing all tasks on the board]
```

**Screenshot After:**
```
[Placeholder: Screenshot showing filtered results based on selected criteria]
```

**Explanation:**
Filtering enables users to view subsets of tasks based on specific criteria: priority level (low/medium/high), tags, or assigned user. Users select the filter type and enter the filter value. The system displays only matching tasks, making it easier to focus on specific categories or priorities.

---

### 12. Generate Report

**Screenshot Before:**
```
[Placeholder: Screenshot showing report menu options]
```

**Screenshot After:**
```
[Placeholder: Screenshot showing generated report with time statistics]
```

**Explanation:**
The reporting system generates three types of reports: daily reports showing time spent on a specific date, weekly reports with daily breakdowns and averages, and overall summary reports with complete project statistics. Reports include total time spent, task counts by status, and time distribution, providing valuable insights into project progress and productivity.

---

### 13. View Task Details

**Screenshot Before:**
```
[Placeholder: Screenshot showing Kanban board view]
```

**Screenshot After:**
```
[Placeholder: Screenshot showing detailed task information display]
```

**Explanation:**
Viewing task details displays comprehensive information about a specific task, including ID, title, description, status, priority, time spent, assigned user, tags, and timer status. This feature provides a complete overview of task properties that may not be fully visible in the compact Kanban board display.

---

### 14. Export to JSON File

**Screenshot Before:**
```
[Placeholder: Screenshot showing export menu option]
```

**Screenshot After:**
```
[Placeholder: Screenshot showing confirmation message and JSON file content]
```

**Explanation:**
The export functionality saves all tasks from the database to a JSON file, including all task properties, tags, and metadata. Users can specify a custom file path or use the default location. This feature enables data backup, migration to other systems, or sharing task data with team members.

---

### 15. Import from JSON File

**Screenshot Before:**
```
[Placeholder: Screenshot showing import menu option]
```

**Screenshot After:**
```
[Placeholder: Screenshot showing imported tasks added to the Kanban board]
```

**Explanation:**
Import functionality restores tasks from a previously exported JSON file. The system reads the JSON data, validates the format, and inserts tasks into the database. This feature enables data restoration, migration from other systems, or bulk task creation, making it easy to set up new project boards or recover from backups.

---

## Project Summary

TaskMate successfully implements a comprehensive Kanban-style project management system with robust features for task organization, time tracking, and data management. The application demonstrates effective use of object-oriented programming principles, database integration with MySQL, and JSON data handling. The system provides a clean console-based interface that visualizes workflow stages through a three-column Kanban board, enabling users to track task progress from creation to completion. Built-in time tracking capabilities allow users to monitor productivity by recording time spent on individual tasks, while advanced search and filtering features facilitate efficient task management in larger projects. The application's dual storage approach—using MySQL for primary persistence and JSON for backup/export—ensures data reliability and portability. TaskMate effectively combines practical project management functionality with solid software engineering practices, making it a valuable tool for individual developers and small teams.

---

## Course Summary

Throughout this Programming 3 course, I gained comprehensive knowledge of core Java programming concepts and their practical applications. The course covered fundamental topics including Java methods, object-oriented programming (OOP) principles with classes and objects, exception handling for robust error management, file operations, and Java I/O streams for data processing. I learned about Java data structures and collections, wrapper classes for primitive type handling, and advanced concepts like JDBC for database connectivity with MySQL. The course emphasized practical application through hands-on projects, teaching me to structure applications using design patterns like DAO (Data Access Object) and service layers. Working with Maven for dependency management and build automation, along with JSON serialization using Gson, provided valuable experience with modern Java development tools. The course significantly enhanced my understanding of software architecture, separation of concerns, and the development of maintainable, production-ready Java applications.

---

**Document Version:** 1.0  
**Date:** 2024  
**Author:** Bhavik Parmar (GM186Q)

