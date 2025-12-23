package com.models;

import com.interfaces.Trackable;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.Duration;

/**
 * ActivitySession model - represents a single Start/Stop time-tracking session
 * Implements Trackable interface to demonstrate interface implementation
 */
public class ActivitySession implements Trackable {
    private int sessionId;
    private int userId;
    private String activityType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int durationMinutes;
    private LocalDate sessionDate;
    private boolean isActive;
    private LocalDateTime createdAt;
    
    public ActivitySession() {
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }
    
    public ActivitySession(int userId, String activityType, LocalDateTime startTime) {
        this();
        this.userId = userId;
        this.activityType = activityType;
        this.startTime = startTime;
        this.sessionDate = startTime.toLocalDate();
    }
    
    /**
     * Stop the activity and calculate duration
     */
    public void stopActivity(LocalDateTime endTime) {
        this.endTime = endTime;
        this.isActive = false;
        
        if (startTime != null && endTime != null) {
            Duration duration = Duration.between(startTime, endTime);
            this.durationMinutes = (int) duration.toMinutes();
        }
    }
    
    /**
     * Calculate duration in hours (for display)
     */
    public double getDurationHours() {
        return durationMinutes / 60.0;
    }
    
    // Getters and setters
    public int getSessionId() { return sessionId; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { 
        this.startTime = startTime;
        if (startTime != null) {
            this.sessionDate = startTime.toLocalDate();
        }
    }
    
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    
    public LocalDate getSessionDate() { return sessionDate; }
    public void setSessionDate(LocalDate sessionDate) { this.sessionDate = sessionDate; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    @Override
    public String toString() {
        return String.format("ActivitySession{id=%d, type='%s', duration=%d min, active=%b}", 
            sessionId, activityType, durationMinutes, isActive);
    }
    
    // Trackable interface implementations
    @Override
    public void startTracking(LocalDateTime startTime) {
        this.startTime = startTime;
        this.sessionDate = startTime.toLocalDate();
        this.isActive = true;
    }
    
    @Override
    public void stopTracking(LocalDateTime endTime) {
        stopActivity(endTime);
    }
    
    @Override
    public String getTrackingStatus() {
        if (isActive) {
            return "Active - " + activityType + " (started at " + 
                   (startTime != null ? startTime.toLocalTime() : "unknown") + ")";
        } else {
            return "Completed - " + activityType + " (" + durationMinutes + " minutes)";
        }
    }
}
