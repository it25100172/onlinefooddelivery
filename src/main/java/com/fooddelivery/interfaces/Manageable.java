package com.fooddelivery.interfaces;

import java.util.List;

/**
 * Manageable interface — defines the CRUD contract for all service classes.
 *
 * OOP Concept: INTERFACE
 *   Interfaces define a contract (a set of methods) that implementing classes must provide.
 *   This ensures all service classes (UserService, MenuService, OrderService) expose
 *   a consistent set of CRUD operations.
 *
 * @param <T>  The entity type this interface manages (e.g., User, MenuItem, Order)
 * @param <ID> The type of the entity's ID (e.g., Integer, String)
 */
public interface Manageable<T, ID> {

    /**
     * CREATE — Adds a new entity and persists it to the data file.
     * @param entity the object to add
     */
    void add(T entity);

    /**
     * UPDATE — Modifies an existing entity in the data file.
     * @param entity the object with updated fields
     */
    void update(T entity);

    /**
     * DELETE — Removes an entity by its ID from the data file.
     * @param id the unique identifier of the entity to remove
     */
    void delete(ID id);

    /**
     * READ — Finds and returns a single entity by its ID.
     * @param id the unique identifier to search for
     * @return the found entity, or null if not found
     */
    T findById(ID id);

    /**
     * READ — Returns all entities stored in the data file.
     * @return a list of all entities
     */
    List<T> findAll();
}
