package com.interfaces;

import java.util.List;

/**
 * Generic Repository Interface for CRUD operations
 * Demonstrates interface usage and generic programming
 */
public interface Repository<T, ID> {
    
    /**
     * Save an entity
     * @param entity The entity to save
     * @return The saved entity with generated ID
     */
    T save(T entity);
    
    /**
     * Find entity by ID
     * @param id The entity ID
     * @return The entity or null if not found
     */
    T findById(ID id);
    
    /**
     * Find all entities
     * @return List of all entities
     */
    List<T> findAll();
    
    /**
     * Update an existing entity
     * @param entity The entity to update
     * @return True if updated successfully
     */
    boolean update(T entity);
    
    /**
     * Delete entity by ID
     * @param id The entity ID to delete
     * @return True if deleted successfully
     */
    boolean deleteById(ID id);
    
    /**
     * Check if entity exists by ID
     * @param id The entity ID
     * @return True if exists
     */
    boolean existsById(ID id);
    
    /**
     * Count total entities
     * @return Total count
     */
    long count();
}