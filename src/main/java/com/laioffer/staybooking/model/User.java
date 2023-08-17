package com.laioffer.staybooking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;

@Entity
@Table(name = "user")
@JsonDeserialize (builder = User.Builder.class)
public class User {
    @Id
    private String username;
    @JsonIgnore            // User这个class的object也可以传送给前端。但是给前端返回是只给username，不返回password 和 enabled信息
    private String password;
    @JsonIgnore
    private boolean enabled;

    public User() {}       //一定要加，要不然不能编译。下面的user constructor是private，需要

    private User(Builder builder) {
        this.username = builder.username;
        this.password = builder.password;
        this.enabled = builder.enabled;
    }

    //getters and setters below.
    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;                          //为什么要return this？
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public User setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    // 为什么要用builder？User user = new User("abc", "123", "true")的话，我们不知道input对应的key是什么。
    // 这里的builder是属于这个class的。
    public static class Builder {   //可以用fluent api。 没有builder的话就需要constructor。Builder pattern是为了解决java的name argument问题
        @JsonProperty("username")
        private String username;

        @JsonProperty("password")
        private String password;

        @JsonProperty("enabled")
        private boolean enabled;

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setEnabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}

// User 不能用 record class因为有些后面用的annotation跟record class不兼容