package com.griddynamics.jagger.jaas.storage;

import com.griddynamics.jagger.jaas.storage.model.DbConfigEntity;

import java.util.List;

/**
 * DAO contract for {@link DbConfigEntity}.
 */
public interface DbConfigEntityDao {
    
    DbConfigEntity read(Long configId);
    
    List<DbConfigEntity> readAll();
    
    void create(DbConfigEntity config);
    
    void update(DbConfigEntity config);
    
    void createOrUpdate(DbConfigEntity config);
    
    void delete(DbConfigEntity config);
}
