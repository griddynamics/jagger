package com.griddynamics.jagger.jaas.storage.model;

import com.griddynamics.jagger.jaas.service.JaggerPropertyName;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@ConfigurationProperties("jagger.db.default")
@Entity
public class DbConfigEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column()
    private String desc;

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


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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
        return "DbConfigEntity{" + "id='" + id + '\'' + ", desc='" + desc + '\'' + ", url='" + url + '\'' + ", user='"
                + user + '\'' + ", pass='" + pass + '\'' + ", jdbcDriver='" + jdbcDriver + '\'' + ", hibernateDialect='"
                + hibernateDialect + '\'' + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        DbConfigEntity that = (DbConfigEntity) obj;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (desc != null ? !desc.equals(that.desc) : that.desc != null) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        if (user != null ? !user.equals(that.user) : that.user != null) return false;
        if (pass != null ? !pass.equals(that.pass) : that.pass != null) return false;
        if (jdbcDriver != null ? !jdbcDriver.equals(that.jdbcDriver) : that.jdbcDriver != null) return false;
        return hibernateDialect != null ? hibernateDialect.equals(that.hibernateDialect) : that.hibernateDialect == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (desc != null ? desc.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (pass != null ? pass.hashCode() : 0);
        result = 31 * result + (jdbcDriver != null ? jdbcDriver.hashCode() : 0);
        result = 31 * result + (hibernateDialect != null ? hibernateDialect.hashCode() : 0);
        return result;
    }
}
