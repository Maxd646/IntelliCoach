package com.database;

import com.models.ActivitySession;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ActivitySession Data Access Object - Core time-tracking operations
 */
public class ActivitySessionDAO {
    private DBConnection dbConnection;
    
    public ActivitySessionDAO() {
        this.dbConnection = DBConnection.getInstance();
    }
    
    /**
     * Start a new activity session
     */
    public ActivitySession startSession(int userId, String activityType, LocalDateTime startTime) {
        // First, stop any active session for this user
        stopActiveSession(userId, startTime);
        
        String sql = "INSERT INTO activity_sessions (user_id, activity_type, start_time, session_date, is_active) " +
                     "VALUES (?, ?, ?, ?, 1)";
        
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setString(2, activityType);
            stmt.setTimestamp(3, Timestamp.valueOf(startTime));
            stmt.setDate(4, Date.valueOf(startTime.toLocalDate()));
            
            int affected = stmt.executeUpdate();
            
            if (affected > 0) {
                // Get the generated ID using SQLite's last_insert_rowid()
                String getIdSql = "SELECT last_insert_rowid()";
                try (PreparedStatement idStmt = dbConnection.getConnection().prepareStatement(getIdSql);
                     ResultSet rs = idStmt.executeQuery()) {
                    if (rs.next()) {
                        ActivitySession session = new ActivitySession(userId, activityType, startTime);
                        session.setSessionId(rs.getInt(1));
                        return session;
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println(" Start session failed: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Stop the active session for a user
     */
    public boolean stopActiveSession(int userId, LocalDateTime endTime) {
        // Get active session
        ActivitySession activeSession = getActiveSession(userId);
        if (activeSession == null) {
            return false;
        }
        
        // Calculate duration
        activeSession.stopActivity(endTime);
        
        String sql = "UPDATE activity_sessions SET end_time = ?, duration_minutes = ?, is_active = 0 " +
                     "WHERE session_id = ?";
        
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(endTime));
            stmt.setInt(2, activeSession.getDurationMinutes());
            stmt.setInt(3, activeSession.getSessionId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println(" Stop session failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get the currently active session for a user
     */
    public ActivitySession getActiveSession(int userId) {
        String sql = "SELECT * FROM activity_sessions WHERE user_id = ? AND is_active = 1 LIMIT 1";
        
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToSession(rs);
            }
            
        } catch (SQLException e) {
            System.err.println(" Get active session failed: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Get all sessions for a user on a specific date
     */
    public List<ActivitySession> getSessionsByDate(int userId, LocalDate date) {
        List<ActivitySession> sessions = new ArrayList<>();
        String sql = "SELECT * FROM activity_sessions WHERE user_id = ? AND session_date = ? " +
                     "ORDER BY start_time ASC";
        
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setDate(2, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                sessions.add(mapResultSetToSession(rs));
            }
            
        } catch (SQLException e) {
            System.err.println(" Get sessions by date failed: " + e.getMessage());
        }
        
        return sessions;
    }
    
    /**
     * Get all sessions for a user in a date range
     */
    public List<ActivitySession> getSessionsByDateRange(int userId, LocalDate startDate, LocalDate endDate) {
        List<ActivitySession> sessions = new ArrayList<>();
        String sql = "SELECT * FROM activity_sessions WHERE user_id = ? " +
                     "AND session_date BETWEEN ? AND ? ORDER BY start_time ASC";
        
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                sessions.add(mapResultSetToSession(rs));
            }
            
        } catch (SQLException e) {
            System.err.println(" Get sessions by date range failed: " + e.getMessage());
        }
        
        return sessions;
    }
    
    /**
     * Get total time spent on each activity type for a date
     */
    public java.util.Map<String, Integer> getTotalTimeByActivity(int userId, LocalDate date) {
        java.util.Map<String, Integer> timeMap = new java.util.HashMap<>();
        String sql = "SELECT activity_type, SUM(duration_minutes) as total_minutes " +
                     "FROM activity_sessions WHERE user_id = ? AND session_date = ? " +
                     "AND duration_minutes IS NOT NULL GROUP BY activity_type";
        
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setDate(2, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                timeMap.put(rs.getString("activity_type"), rs.getInt("total_minutes"));
            }
            
        } catch (SQLException e) {
            System.err.println(" Get total time failed: " + e.getMessage());
        }
        
        return timeMap;
    }
    
    /**
     * Map ResultSet to ActivitySession object
     */
    private ActivitySession mapResultSetToSession(ResultSet rs) throws SQLException {
        ActivitySession session = new ActivitySession();
        session.setSessionId(rs.getInt("session_id"));
        session.setUserId(rs.getInt("user_id"));
        session.setActivityType(rs.getString("activity_type"));
        
        Timestamp startTime = rs.getTimestamp("start_time");
        if (startTime != null) {
            session.setStartTime(startTime.toLocalDateTime());
        }
        
        Timestamp endTime = rs.getTimestamp("end_time");
        if (endTime != null) {
            session.setEndTime(endTime.toLocalDateTime());
        }
        
        session.setDurationMinutes(rs.getInt("duration_minutes"));
        
        Date sessionDate = rs.getDate("session_date");
        if (sessionDate != null) {
            session.setSessionDate(sessionDate.toLocalDate());
        }
        
        session.setActive(rs.getBoolean("is_active"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            session.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return session;
    }
}
