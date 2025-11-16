package com.finalproject.util;

import com.finalproject.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Utility class for generating simple sequential task IDs
 */
public class IdGenerator {
    
    /**
     * Generate next simple task ID (T1, T2, T3, etc.)
     */
    public static String generateNextTaskId() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            
            // Get the maximum numeric ID from existing tasks
            String query = "SELECT MAX(CAST(SUBSTRING(id, 2) AS UNSIGNED)) as max_id FROM tasks WHERE id REGEXP '^T[0-9]+$'";
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            
            int nextId = 1;
            if (rs.next()) {
                int maxId = rs.getInt("max_id");
                if (!rs.wasNull() && maxId > 0) {
                    nextId = maxId + 1;
                }
            }
            
            return "T" + nextId;
            
        } catch (SQLException e) {
            // If query fails, fall back to timestamp-based ID
            System.err.println("Warning: Could not generate sequential ID, using timestamp-based ID");
            return "T" + System.currentTimeMillis();
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
    }
}

