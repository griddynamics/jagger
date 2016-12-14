package com.griddynamics.jagger.jaas.storage;

import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Collection;


/**
 * Abstract implementation of {@link CrudDao}. It implements all methods by throwing {@link UnsupportedOperationException}.
 * Captures the domain type to manage as well as the domain type's id type.
 * Implementations extending this class MUST override all needed methods.
 *
 * @param <T>  the domain type the dao manages
 * @param <ID> the type of the id of the entity the dao manages
 */
public abstract class AbstractCrudDao<T, ID extends Serializable> implements CrudDao<T, ID> {
    @Autowired
    private SessionFactory sessionFactory;

    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public void create(T entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void create(Iterable<T> entities) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T read(ID id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<T> readAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<T> readAll(Iterable<ID> ids) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(T entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void createOrUpdate(T entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(ID id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(T entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Iterable<T> entities) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean exists(ID id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long count() {
        throw new UnsupportedOperationException();
    }
}
