package com.griddynamics.jagger.test.jaas.util.entity;

import com.alibaba.fastjson.JSON;

import java.util.Objects;

/**
 * A value object representing JaaS' DB config entity.
 * Created by ELozovan on 2016-10-11.
 */
public class DbConfigEntity {
    private Long id;

    private String desc;

    private String url;

    private String user;

    private String pass;

    private String jdbcDriver;

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
        final StringBuilder sb = new StringBuilder("DbConfigEntity{");
        sb.append("id=").append(id);
        sb.append(", desc='").append(desc).append('\'');
        sb.append(", url='").append(url).append('\'');
        sb.append(", user='").append(user).append('\'');
        sb.append(", pass='").append(pass).append('\'');
        sb.append(", jdbcDriver='").append(jdbcDriver).append('\'');
        sb.append(", hibernateDialect='").append(hibernateDialect).append('\'');
        sb.append('}');
        return sb.toString();
    }

    /**
     * Returns serialised instance.
     */
    public String toJson(){
        return JSON.toJSONString(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbConfigEntity)) return false;
        DbConfigEntity that = (DbConfigEntity) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getDesc(), that.getDesc()) &&
                Objects.equals(getUrl(), that.getUrl()) &&
                Objects.equals(getUser(), that.getUser()) &&
                Objects.equals(getPass(), that.getPass()) &&
                Objects.equals(getJdbcDriver(), that.getJdbcDriver()) &&
                Objects.equals(getHibernateDialect(), that.getHibernateDialect());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getDesc(), getUrl(), getUser(), getPass(), getJdbcDriver(), getHibernateDialect());
    }
}