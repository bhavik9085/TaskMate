-- TaskMate Database Schema
-- Run this script to create the database and tables for TaskMate

CREATE DATABASE IF NOT EXISTS taskmate_db;
USE taskmate_db;

-- Drop tables if they exist (for clean setup)
DROP TABLE IF EXISTS task_tags;
DROP TABLE IF EXISTS tasks;

-- Create tasks table
CREATE TABLE tasks (
    id VARCHAR(100) PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'To-Do',
    priority VARCHAR(10) NOT NULL DEFAULT 'medium',
    time_spent FLOAT DEFAULT 0.0,
    start_time VARCHAR(50),
    end_time VARCHAR(50),
    assigned_to VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status (status),
    INDEX idx_priority (priority),
    INDEX idx_assigned_to (assigned_to)
);

-- Create task_tags junction table for many-to-many relationship
CREATE TABLE task_tags (
    task_id VARCHAR(100),
    tag VARCHAR(50),
    PRIMARY KEY (task_id, tag),
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    INDEX idx_tag (tag)
);

-- Insert sample data (optional)
INSERT INTO tasks (id, title, description, status, priority, time_spent, assigned_to) VALUES
('TASK-001', 'Implement login system', 'Create user authentication module', 'In-Progress', 'high', 2.5, 'Bhavik'),
('TASK-002', 'Fix dashboard alignment', 'Fix CSS alignment issues', 'To-Do', 'high', 0.0, NULL),
('TASK-003', 'Write unit tests', 'Add test coverage for core modules', 'Done', 'medium', 5.0, 'Bhavik');

INSERT INTO task_tags (task_id, tag) VALUES
('TASK-001', 'backend'),
('TASK-001', 'authentication'),
('TASK-002', 'frontend'),
('TASK-002', 'css'),
('TASK-003', 'testing');

-- Verify the tables were created
SELECT * FROM tasks;
SELECT * FROM task_tags;
