package com.griddynamics.jagger.jaas.storage;

import com.griddynamics.jagger.jaas.storage.model.JaggerTestDbConfig;

import java.util.List;

/**
 * DAO contract for {@link com.griddynamics.jagger.jaas.storage.model.JaggerTestDbConfig}.
 */
public interface JaggerTestDbConfigDao {
    
    JaggerTestDbConfig read(String configName);
    
    List<JaggerTestDbConfig> readAll();
    
    void createOrUpdate(JaggerTestDbConfig config);
    
    void delete(JaggerTestDbConfig config);
}
