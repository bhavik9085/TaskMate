package com.finalproject.dao;

import com.finalproject.config.DatabaseConfig;
import com.finalproject.model.Task;
import com.finalproject.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Task entity - MySQL database operations
 */
public class TaskDAO {
    
    private static final String INSERT_TASK = 
        "INSERT INTO tasks (id, title, description, status, priority, time_spent, start_time, end_time, assigned_to, created_at, updated_at) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String INSERT_TASK_TAG = 
        "INSERT INTO task_tags (task_id, tag) VALUES (?, ?)";
    
    private static final String SELECT_ALL_TASKS = 
        "SELECT t.*, GROUP_CONCAT(tt.tag) as tags FROM tasks t " +
        "LEFT JOIN task_tags tt ON t.id = tt.task_id " +
        "GROUP BY t.id ORDER BY t.created_at DESC";
    
    private static final String SELECT_TASK_BY_ID = 
        "SELECT t.*, GROUP_CONCAT(tt.tag) as tags FROM tasks t " +
        "LEFT JOIN task_tags tt ON t.id = tt.task_id " +
        "WHERE t.id = ? GROUP BY t.id";
    
    private static final String SELECT_TASKS_BY_STATUS = 
        "SELECT t.*, GROUP_CONCAT(tt.tag) as tags FROM tasks t " +
        "LEFT JOIN task_tags tt ON t.id = tt.task_id " +
        "WHERE t.status = ? GROUP BY t.id ORDER BY t.created_at DESC";
    
    private static final String UPDATE_TASK = 
        "UPDATE tasks SET title = ?, description = ?, status = ?, priority = ?, " +
        "time_spent = ?, start_time = ?, end_time = ?, assigned_to = ?, updated_at = ? " +
        "WHERE id = ?";
    
    private static final String DELETE_TASK_TAGS = 
        "DELETE FROM task_tags WHERE task_id = ?";
    
    private static final String DELETE_TASK = 
        "DELETE FROM tasks WHERE id = ?";
    
    private static final String SEARCH_TASKS = 
        "SELECT t.*, GROUP_CONCAT(tt.tag) as tags FROM tasks t " +
        "LEFT JOIN task_tags tt ON t.id = tt.task_id " +
        "WHERE t.title LIKE ? OR t.description LIKE ? " +
        "GROUP BY t.id ORDER BY t.created_at DESC";
    
    private static final String SELECT_TASKS_BY_PRIORITY = 
        "SELECT t.*, GROUP_CONCAT(tt.tag) as tags FROM tasks t " +
        "LEFT JOIN task_tags tt ON t.id = tt.task_id " +
        "WHERE t.priority = ? GROUP BY t.id ORDER BY t.created_at DESC";
    
    private static final String SELECT_TASKS_BY_TAG = 
        "SELECT t.*, GROUP_CONCAT(tt.tag) as tags FROM tasks t " +
        "INNER JOIN task_tags tt ON t.id = tt.task_id " +
        "WHERE tt.tag = ? GROUP BY t.id ORDER BY t.created_at DESC";
    
    private static final String SELECT_TASKS_BY_ASSIGNED = 
        "SELECT t.*, GROUP_CONCAT(tt.tag) as tags FROM tasks t " +
        "LEFT JOIN task_tags tt ON t.id = tt.task_id " +
        "WHERE t.assigned_to = ? GROUP BY t.id ORDER BY t.created_at DESC";

    /**
     * Insert a new task
     */
    public boolean insertTask(Task task) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // Insert task
            pstmt = conn.prepareStatement(INSERT_TASK);
            pstmt.setString(1, task.getId());
            pstmt.setString(2, task.getTitle());
            pstmt.setString(3, task.getDescription());
            pstmt.setString(4, task.getStatus());
            pstmt.setString(5, task.getPriority());
            pstmt.setFloat(6, task.getTimeSpent());
            pstmt.setString(7, task.getStartTime());
            pstmt.setString(8, task.getEndTime());
            pstmt.setString(9, task.getAssignedTo());
            pstmt.setTimestamp(10, Timestamp.valueOf(task.getCreatedAt()));
            pstmt.setTimestamp(11, Timestamp.valueOf(task.getUpdatedAt()));
            
            int rowsAffected = pstmt.executeUpdate();
            pstmt.close();
            
            // Insert tags
            if (rowsAffected > 0 && task.getTags() != null && !task.getTags().isEmpty()) {
                pstmt = conn.prepareStatement(INSERT_TASK_TAG);
                for (String tag : task.getTags()) {
                    pstmt.setString(1, task.getId());
                    pstmt.setString(2, tag);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error rolling back transaction: " + ex.getMessage());
                }
            }
            System.err.println("Error inserting task: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DatabaseUtil.closeAll(conn, pstmt);
        }
    }

    /**
     * Get all tasks
     */
    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            pstmt = conn.prepareStatement(SELECT_ALL_TASKS);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                tasks.add(mapResultSetToTask(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving tasks: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        
        return tasks;
    }

    /**
     * Get task by ID
     */
    public Task getTaskById(String id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            pstmt = conn.prepareStatement(SELECT_TASK_BY_ID);
            pstmt.setString(1, id);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToTask(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving task: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        
        return null;
    }

    /**
     * Get tasks by status
     */
    public List<Task> getTasksByStatus(String status) {
        List<Task> tasks = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            pstmt = conn.prepareStatement(SELECT_TASKS_BY_STATUS);
            pstmt.setString(1, status);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                tasks.add(mapResultSetToTask(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving tasks by status: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        
        return tasks;
    }

    /**
     * Update task
     */
    public boolean updateTask(Task task) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            
            // Update task
            pstmt = conn.prepareStatement(UPDATE_TASK);
            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setString(3, task.getStatus());
            pstmt.setString(4, task.getPriority());
            pstmt.setFloat(5, task.getTimeSpent());
            pstmt.setString(6, task.getStartTime());
            pstmt.setString(7, task.getEndTime());
            pstmt.setString(8, task.getAssignedTo());
            pstmt.setTimestamp(9, Timestamp.valueOf(task.getUpdatedAt()));
            pstmt.setString(10, task.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            pstmt.close();
            
            // Update tags - delete old and insert new
            if (rowsAffected > 0) {
                pstmt = conn.prepareStatement(DELETE_TASK_TAGS);
                pstmt.setString(1, task.getId());
                pstmt.executeUpdate();
                pstmt.close();
                
                if (task.getTags() != null && !task.getTags().isEmpty()) {
                    pstmt = conn.prepareStatement(INSERT_TASK_TAG);
                    for (String tag : task.getTags()) {
                        pstmt.setString(1, task.getId());
                        pstmt.setString(2, tag);
                        pstmt.addBatch();
                    }
                    pstmt.executeBatch();
                }
            }
            
            conn.commit();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error rolling back transaction: " + ex.getMessage());
                }
            }
            System.err.println("Error updating task: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DatabaseUtil.closeAll(conn, pstmt);
        }
    }

    /**
     * Delete task
     */
    public boolean deleteTask(String id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            
            // Delete tags first (foreign key constraint)
            pstmt = conn.prepareStatement(DELETE_TASK_TAGS);
            pstmt.setString(1, id);
            pstmt.executeUpdate();
            pstmt.close();
            
            // Delete task
            pstmt = conn.prepareStatement(DELETE_TASK);
            pstmt.setString(1, id);
            int rowsAffected = pstmt.executeUpdate();
            
            conn.commit();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error rolling back transaction: " + ex.getMessage());
                }
            }
            System.err.println("Error deleting task: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DatabaseUtil.closeAll(conn, pstmt);
        }
    }

    /**
     * Search tasks
     */
    public List<Task> searchTasks(String searchTerm) {
        List<Task> tasks = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            pstmt = conn.prepareStatement(SEARCH_TASKS);
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                tasks.add(mapResultSetToTask(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error searching tasks: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        
        return tasks;
    }

    /**
     * Filter by priority
     */
    public List<Task> filterByPriority(String priority) {
        List<Task> tasks = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            pstmt = conn.prepareStatement(SELECT_TASKS_BY_PRIORITY);
            pstmt.setString(1, priority);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                tasks.add(mapResultSetToTask(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error filtering by priority: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        
        return tasks;
    }

    /**
     * Filter by tag
     */
    public List<Task> filterByTag(String tag) {
        List<Task> tasks = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            pstmt = conn.prepareStatement(SELECT_TASKS_BY_TAG);
            pstmt.setString(1, tag);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                tasks.add(mapResultSetToTask(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error filtering by tag: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        
        return tasks;
    }

    /**
     * Filter by assigned user
     */
    public List<Task> filterByAssignedUser(String user) {
        List<Task> tasks = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            pstmt = conn.prepareStatement(SELECT_TASKS_BY_ASSIGNED);
            pstmt.setString(1, user);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                tasks.add(mapResultSetToTask(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error filtering by assigned user: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        
        return tasks;
    }

    /**
     * Map ResultSet to Task object
     */
    private Task mapResultSetToTask(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setId(rs.getString("id"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));
        task.setStatus(rs.getString("status"));
        task.setPriority(rs.getString("priority"));
        task.setTimeSpent(rs.getFloat("time_spent"));
        task.setStartTime(rs.getString("start_time"));
        task.setEndTime(rs.getString("end_time"));
        task.setAssignedTo(rs.getString("assigned_to"));
        
        // Parse tags from GROUP_CONCAT result
        String tagsStr = rs.getString("tags");
        if (tagsStr != null && !tagsStr.isEmpty()) {
            String[] tags = tagsStr.split(",");
            for (String tag : tags) {
                task.addTag(tag.trim());
            }
        }
        
        // Parse timestamps
        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (createdAt != null) {
            task.setCreatedAt(createdAt.toLocalDateTime());
        }
        if (updatedAt != null) {
            task.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return task;
    }
}

