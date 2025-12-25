package com.interfaces;

import java.time.LocalDateTime;

/**
 * Interface for trackable activitie
 */
public interface Trackable {

    /**
     * Start tracking
     */
    void startTracking(LocalDateTime startTime);

    /**
     * Stop tracking
     */
    void stopTracking(LocalDateTime endTime);

    /**
     * Check if currently being tracked
     */
    boolean isActive();

    /**
     * Get duration in minutes
     */
    int getDurationMinutes();

    /**
     * Get tracking status
     */
    String getTrackingStatus();
}