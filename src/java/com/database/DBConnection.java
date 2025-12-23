package com.database;

import java.sql.*;
import java.io.File;

/**
 * Database connection manager - Singleton pattern
 */
public class DBConnection {
    private static DBConnection instance;
    private Connection connection;
    private static final String DB_PATH = "database/intellicoach.db";

    private DBConnection() {
        connect();
    }

    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    private void connect() {
        try {
            // Create database directory if it doesn't exist
            File dbFile = new File(DB_PATH);
            File dbDir = dbFile.getParentFile();
            if (dbDir != null && !dbDir.exists()) {
                dbDir.mkdirs();
            }

            // Load SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");

            // Connect to database
            String url = "jdbc:sqlite:" + DB_PATH;
            connection = DriverManager.getConnection(url);
            connection.setAutoCommit(true);

            System.out.println(" Database connected: " + DB_PATH);

            // Initialize schema
            initializeSchema();

        } catch (ClassNotFoundException e) {
            System.err.println(" SQLite JDBC driver not found!");
            System.err.println(
                    "   Download: https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.44.1.0/sqlite-jdbc-3.44.1.0.jar");
            System.err.println("   Place in: lib/sqlite-jdbc-3.44.1.0.jar");
        } catch (SQLException e) {
            System.err.println(" Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeSchema() {
        try {
            Statement stmt = connection.createStatement();

            // Read and execute schema from file or use embedded schema
            String schema = getSchemaSQL();
            String[] statements = schema.split(";");

            for (String sql : statements) {
                if (sql.trim().length() > 0) {
                    stmt.execute(sql.trim());
                }
            }

            System.out.println(" Database schema initialized");

        } catch (SQLException e) {
            System.err.println(" Schema initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getSchemaSQL() {
        return """
                CREATE TABLE IF NOT EXISTS users (
                    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    email VARCHAR(100) UNIQUE NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    full_name VARCHAR(100),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );

                CREATE TABLE IF NOT EXISTS activity_sessions (
                    session_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    activity_type VARCHAR(50) NOT NULL,
                    start_time TIMESTAMP NOT NULL,
                    end_time TIMESTAMP,
                    duration_minutes INTEGER,
                    session_date DATE NOT NULL,
                    is_active BOOLEAN DEFAULT 1,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES users(user_id),
                    CHECK (activity_type IN ('Academic', 'Sport', 'Entertainment', 'Extra Activity', 'Sleep', 'Health / Hygiene'))
                );

                CREATE TABLE IF NOT EXISTS recommended_times (
                    activity_type VARCHAR(50) PRIMARY KEY,
                    min_minutes INTEGER NOT NULL,
                    max_minutes INTEGER NOT NULL,
                    description TEXT
                );

                CREATE TABLE IF NOT EXISTS recommendations (
                    recommendation_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    recommendation_text TEXT NOT NULL,
                    recommendation_type VARCHAR(50) NOT NULL,
                    priority VARCHAR(20) DEFAULT 'MEDIUM',
                    based_on_date DATE NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    is_read BOOLEAN DEFAULT 0,
                    FOREIGN KEY (user_id) REFERENCES users(user_id)
                );

                INSERT OR REPLACE INTO recommended_times (activity_type, min_minutes, max_minutes, description) VALUES
                ('Academic', 360, 480, 'Study, homework, classes - 6 to 8 hours per day'),
                ('Sleep', 420, 540, 'Essential rest - 7 to 9 hours per day'),
                ('Sport', 60, 120, 'Physical exercise - 1 to 2 hours per day'),
                ('Entertainment', 60, 120, 'Leisure, games, social media - 1 to 2 hours per day'),
                ('Extra Activity', 60, 120, 'Clubs, volunteering, hobbies - 1 to 2 hours per day'),
                ('Health / Hygiene', 30, 60, 'Personal care, meals - 30 to 60 minutes per day');

                CREATE INDEX IF NOT EXISTS idx_sessions_user_date ON activity_sessions(user_id, session_date);
                CREATE INDEX IF NOT EXISTS idx_sessions_active ON activity_sessions(user_id, is_active);
                CREATE INDEX IF NOT EXISTS idx_recommendations_user ON recommendations(user_id, created_at);
                """;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            System.err.println(" Connection check failed: " + e.getMessage());
        }
        return connection;
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println(" Database disconnected");
            }
        } catch (SQLException e) {
            System.err.println(" Disconnect failed: " + e.getMessage());
        }
    }
}
