package com.interfaces;

import java.util.List;

/**
 * Generic Repository Interface for CRUD operations
 */
public interface Repository<T, ID> {

    /**
     * Save an entity
     */
    T save(T entity);

    /**
     * Find entity by ID
     */
    T findById(ID id);

    /**
     * Find all entities
     */
    List<T> findAll();

    /**
     * Update an existing entity
     * 
     */
    boolean update(T entity);

    boolean deleteById(ID id);

    boolean existsById(ID id);

    long count();
}