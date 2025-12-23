package com.services;

import com.models.ActivitySession;
import com.database.ActivitySessionDAO;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Time Tracking Service - Core Start/Stop functionality
 * Handles activity session management
 */
public class TimeTrackingService {
    private ActivitySessionDAO sessionDAO;
    
    public TimeTrackingService() {
        this.sessionDAO = new ActivitySessionDAO();
    }
    
    /**
     * Start a new activity
     * Automatically stops any currently active activity
     */
    public ActivitySession startActivity(int userId, String activityType) {
        LocalDateTime now = LocalDateTime.now();
        return sessionDAO.startSession(userId, activityType, now);
    }
    
    /**
     * Stop the currently active activity
     */
    public boolean stopActivity(int userId) {
        LocalDateTime now = LocalDateTime.now();
        return sessionDAO.stopActiveSession(userId, now);
    }
    
    /**
     * Get the currently active session
     */
    public ActivitySession getActiveSession(int userId) {
        return sessionDAO.getActiveSession(userId);
    }
    
    /**
     * Check if user has an active session
     */
    public boolean hasActiveSession(int userId) {
        return sessionDAO.getActiveSession(userId) != null;
    }
    
    /**
     * Get all sessions for today
     */
    public List<ActivitySession> getTodaySessions(int userId) {
        return sessionDAO.getSessionsByDate(userId, LocalDate.now());
    }
    
    /**
     * Get sessions for a specific date
     */
    public List<ActivitySession> getSessionsByDate(int userId, LocalDate date) {
        return sessionDAO.getSessionsByDate(userId, date);
    }
    
    /**
     * Get sessions for a date range
     */
    public List<ActivitySession> getSessionsByDateRange(int userId, LocalDate startDate, LocalDate endDate) {
        return sessionDAO.getSessionsByDateRange(userId, startDate, endDate);
    }
    
    /**
     * Get total time spent on each activity for today
     */
    public Map<String, Integer> getTodayTimeByActivity(int userId) {
        return sessionDAO.getTotalTimeByActivity(userId, LocalDate.now());
    }
    
    /**
     * Get total time spent on each activity for a specific date
     */
    public Map<String, Integer> getTimeByActivity(int userId, LocalDate date) {
        return sessionDAO.getTotalTimeByActivity(userId, date);
    }
    
    /**
     * Get total time for all activities today (in minutes)
     */
    public int getTotalTimeToday(int userId) {
        Map<String, Integer> timeMap = getTodayTimeByActivity(userId);
        return timeMap.values().stream().mapToInt(Integer::intValue).sum();
    }
    
    /**
     * Get time spent on a specific activity type today (in minutes)
     */
    public int getTimeForActivityToday(int userId, String activityType) {
        Map<String, Integer> timeMap = getTodayTimeByActivity(userId);
        return timeMap.getOrDefault(activityType, 0);
    }
}
