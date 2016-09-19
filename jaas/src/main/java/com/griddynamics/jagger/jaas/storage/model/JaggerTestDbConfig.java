package com.griddynamics.jagger.jaas.storage.model;

import com.griddynamics.jagger.jaas.service.JaggerPropertyName;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@ConfigurationProperties("jagger.db.default")
@Entity
public class JaggerTestDbConfig {
    
    @NotNull
    @Id
    private String name;
    
    @NotNull
    @Column(nullable = false)
    @JaggerPropertyName("chassis.storage.rdb.client.url")
    private String url;
    
    @NotNull
    @Column(nullable = false)
    @JaggerPropertyName("chassis.storage.rdb.username")
    private String user;
    
    @NotNull
    @Column(nullable = false)
    @JaggerPropertyName("chassis.storage.rdb.password")
    private String pass;
    
    @NotNull
    @Column(nullable = false)
    @JaggerPropertyName("chassis.storage.rdb.client.driver")
    private String jdbcDriver;
    
    @NotNull
    @Column(nullable = false)
    @JaggerPropertyName("chassis.storage.hibernate.dialect")
    private String hibernateDialect;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getUser() {
        return user;
    }
    
    public void setUser(String user) {
        this.user = user;
    }
    
    public String getPass() {
        return pass;
    }
    
    public void setPass(String pass) {
        this.pass = pass;
    }
    
    public String getJdbcDriver() {
        return jdbcDriver;
    }
    
    public void setJdbcDriver(String jdbcDriver) {
        this.jdbcDriver = jdbcDriver;
    }
    
    public String getHibernateDialect() {
        return hibernateDialect;
    }
    
    public void setHibernateDialect(String hibernateDialect) {
        this.hibernateDialect = hibernateDialect;
    }
    
    @Override
    public String toString() {
        return "JaggerTestDbConfig{" + "name='" + name + '\'' + ", url='" + url + '\'' + ", user='" + user + '\''
               + ", pass='" + pass + '\'' + ", jdbcDriver='" + jdbcDriver + '\'' + ", hibernateDialect='"
               + hibernateDialect + '\'' + '}';
    }
}
