package com.interfaces;

/**
 * Base Service Interface
 * Defines common service operations
 */
public interface Service {
    
    /**
     * Initialize the service
     * @return True if initialization successful
     */
    boolean initialize();
    
    /**
     * Check if service is ready
     * @return True if service is ready to use
     */
    boolean isReady();
    
    /**
     * Get service name
     * @return Service name
     */
    String getServiceName();
    
    /**
     * Cleanup resources
     */
    void cleanup();
}