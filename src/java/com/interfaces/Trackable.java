package com.interfaces;

import java.time.LocalDateTime;

/**
 * Interface for trackable activities
 * Demonstrates interface segregation principle
 */
public interface Trackable {
    
    /**
     * Start tracking
     * @param startTime The start time
     */
    void startTracking(LocalDateTime startTime);
    
    /**
     * Stop tracking
     * @param endTime The end time
     */
    void stopTracking(LocalDateTime endTime);
    
    /**
     * Check if currently being tracked
     * @return True if active
     */
    boolean isActive();
    
    /**
     * Get duration in minutes
     * @return Duration in minutes
     */
    int getDurationMinutes();
    
    /**
     * Get tracking status
     * @return Status description
     */
    String getTrackingStatus();
}