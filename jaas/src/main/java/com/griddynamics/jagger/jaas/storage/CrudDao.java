package com.griddynamics.jagger.jaas.storage;

import java.io.Serializable;
import java.util.Collection;

/**
 * Interface for generic CRUD operations on a repository for a specific type.
 */
public interface CrudDao<T, ID extends Serializable> {
    /**
     * Saves a given entity.
     *
     * @param entity entity to create.
     */
    void create(T entity);

    /**
     * Saves all given entities.
     *
     * @param entities entities to create.
     * @throws IllegalArgumentException in case the given entity is {@literal null}.
     */
    void create(Iterable<T> entities);

    /**
     * Retrieves an entity by its id.
     *
     * @param id must not be {@literal null}.
     * @return the entity with the given id or {@literal null} if none found
     * @throws IllegalArgumentException if {@code id} is {@literal null}
     */
    T read(ID id);

    /**
     * Returns all instances of the type.
     *
     * @return all entities
     */
    Collection<T> readAll();

    /**
     * Returns all instances of the type with the given IDs.
     *
     * @param ids ids of entities to read.
     * @return all entities with given IDs.
     */
    Collection<T> readAll(Iterable<ID> ids);

    /**
     * Updates a given entity.
     *
     * @param entity entity to update.
     */
    void update(T entity);

    /**
     * Creates a given entity if it doesn't exist or updates it otherwise.
     *
     * @param entity entity to create or upddate.
     */
    void createOrUpdate(T entity);

    /**
     * Deletes the entity with the given id.
     *
     * @param id must not be {@literal null}.
     * @throws IllegalArgumentException in case the given {@code id} is {@literal null}
     */
    void delete(ID id);

    /**
     * Deletes a given entity.
     *
     * @param entity entity to delete.
     * @throws IllegalArgumentException in case the given entity is {@literal null}.
     */
    void delete(T entity);

    /**
     * Deletes the given entities.
     *
     * @param entities entities to delete.
     * @throws IllegalArgumentException in case the given {@link Iterable} is {@literal null}.
     */
    void delete(Iterable<T> entities);

    /**
     * Deletes all entities managed by the repository.
     */
    void deleteAll();

    /**
     * Returns whether an entity with the given id exists.
     *
     * @param id must not be {@literal null}.
     * @return true if an entity with the given id exists, {@literal false} otherwise
     * @throws IllegalArgumentException if {@code id} is {@literal null}
     */
    boolean exists(ID id);

    /**
     * Returns the number of entities available.
     *
     * @return the number of entities
     */
    long count();
}
