package com.interfaces;

/**
 * Base Service Interface
 * Defines common service operations
 */
public interface Service {

    /**
     * Initialize the service
     */
    boolean initialize();

    /**
     * Check if service is ready
     */
    boolean isReady();

    /**
     * Get service name
     */
    String getServiceName();

    /**
     * Cleanup resources
     */
    void cleanup();
}