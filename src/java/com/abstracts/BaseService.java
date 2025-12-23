package com.abstracts;

import com.interfaces.Service;

/**
 * Abstract Base Service Class
 * Provides common functionality for all services
 */
public abstract class BaseService implements Service {
    
    protected boolean initialized = false;
    protected String serviceName;
    
    public BaseService(String serviceName) {
        this.serviceName = serviceName;
    }
    
    @Override
    public final boolean initialize() {
        if (initialized) {
            return true;
        }
        
        try {
            // Template method pattern - calls abstract method
            doInitialize();
            initialized = true;
            onInitializationComplete();
            return true;
        } catch (Exception e) {
            onInitializationError(e);
            return false;
        }
    }
    
    /**
     * Abstract method for specific initialization logic
     * Must be implemented by concrete classes
     */
    protected abstract void doInitialize() throws Exception;
    
    /**
     * Hook method called after successful initialization
     * Can be overridden by subclasses
     */
    protected void onInitializationComplete() {
        System.out.println(serviceName + " service initialized successfully");
    }
    
    /**
     * Hook method called when initialization fails
     * Can be overridden by subclasses
     */
    protected void onInitializationError(Exception e) {
        System.err.println(serviceName + " service initialization failed: " + e.getMessage());
    }
    
    @Override
    public boolean isReady() {
        return initialized;
    }
    
    @Override
    public String getServiceName() {
        return serviceName;
    }
    
    @Override
    public void cleanup() {
        if (initialized) {
            doCleanup();
            initialized = false;
            System.out.println(serviceName + " service cleaned up");
        }
    }
    
    /**
     * Abstract method for specific cleanup logic
     * Must be implemented by concrete classes
     */
    protected abstract void doCleanup();
    
    /**
     * Utility method to validate service is ready
     * @throws IllegalStateException if service not initialized
     */
    protected final void ensureInitialized() {
        if (!initialized) {
            throw new IllegalStateException(serviceName + " service is not initialized");
        }
    }
}