package com.finalproject.util;

import com.finalproject.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

/**
 * Utility class to set up database tables if they don't exist
 */
public class DatabaseSetup {
    
    /**
     * Create tables if they don't exist
     */
    public static boolean createTablesIfNotExist() {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.createStatement();
            
            // Create tasks table
            String createTasksTable = 
                "CREATE TABLE IF NOT EXISTS tasks (" +
                "id VARCHAR(100) PRIMARY KEY, " +
                "title VARCHAR(200) NOT NULL, " +
                "description TEXT, " +
                "status VARCHAR(20) NOT NULL DEFAULT 'To-Do', " +
                "priority VARCHAR(10) NOT NULL DEFAULT 'medium', " +
                "time_spent FLOAT DEFAULT 0.0, " +
                "start_time VARCHAR(50), " +
                "end_time VARCHAR(50), " +
                "assigned_to VARCHAR(100), " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                "INDEX idx_status (status), " +
                "INDEX idx_priority (priority), " +
                "INDEX idx_assigned_to (assigned_to)" +
                ")";
            
            stmt.executeUpdate(createTasksTable);
            
            // Create task_tags table
            String createTaskTagsTable = 
                "CREATE TABLE IF NOT EXISTS task_tags (" +
                "task_id VARCHAR(100), " +
                "tag VARCHAR(50), " +
                "PRIMARY KEY (task_id, tag), " +
                "FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE, " +
                "INDEX idx_tag (tag)" +
                ")";
            
            stmt.executeUpdate(createTaskTagsTable);
            
            System.out.println("✓ Database tables created successfully!");
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }
    }
    
    /**
     * Check if tables exist
     */
    public static boolean tablesExist() {
        Connection conn = null;
        Statement stmt = null;
        java.sql.ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.createStatement();
            
            // Check if tasks table exists
            rs = stmt.executeQuery(
                "SELECT COUNT(*) FROM information_schema.tables " +
                "WHERE table_schema = DATABASE() AND table_name = 'tasks'"
            );
            
            if (rs.next() && rs.getInt(1) > 0) {
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            return false;
        } finally {
            DatabaseUtil.closeAll(conn, stmt, rs);
        }
    }
}

