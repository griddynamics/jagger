package com.griddynamics.jagger.jaas.service;

import com.griddynamics.jagger.engine.e1.services.DataService;
import com.griddynamics.jagger.jaas.storage.model.DbConfigEntity;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Properties;

public interface DynamicDataService {
    
    Long DEFAULT_DB_CONFIG_ID = -1L;
    
    DataService getDataServiceFor(Long configId);

    ApplicationContext getDynamicContextFor(Long configId);

    Properties extractPropsFrom(DbConfigEntity config);

    DbConfigEntity read(Long configId);

    List<DbConfigEntity> readAll();

    void create(DbConfigEntity config);

    void update(DbConfigEntity config);

    void createOrUpdate(DbConfigEntity config);

    void delete(DbConfigEntity config);
}
