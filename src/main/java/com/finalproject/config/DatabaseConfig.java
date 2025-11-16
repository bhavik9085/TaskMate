package com.finalproject.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Database configuration and connection management
 */
public class DatabaseConfig {
    private static final String CONFIG_FILE = "src/main/resources/database.properties";
    private static String url;
    private static String username;
    private static String password;
    private static String driver;

    static {
        loadProperties();
    }

    /**
     * Load database properties from configuration file
     */
    private static void loadProperties() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            props.load(fis);
            url = props.getProperty("db.url");
            username = props.getProperty("db.username");
            password = props.getProperty("db.password");
            driver = props.getProperty("db.driver");
            
            // Load the driver class
            Class.forName(driver);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading database configuration: " + e.getMessage());
            // Set default values for MySQL
            url = "jdbc:mysql://localhost:3306/finalproject_db";
            username = "root";
            password = "password";
            driver = "com.mysql.cj.jdbc.Driver";
            try {
                Class.forName(driver);
            } catch (ClassNotFoundException ex) {
                System.err.println("MySQL driver not found!");
            }
        }
    }

    /**
     * Get a database connection
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Test database connection
     * @return true if connection successful
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }

    public static String getUrl() {
        return url;
    }

    public static String getUsername() {
        return username;
    }
}

