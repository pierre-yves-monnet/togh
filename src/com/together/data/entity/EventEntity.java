package com.together.data.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.json.simple.JSONValue;

import com.together.data.entity.base.BaseEntity;
import com.together.data.entity.base.UserEntity;

// see https://github.com/spring-projects/spring-data-book/blob/master/jpa/src/main/java/com/oreilly/springdata/jpa/core/Customer.java

@Entity
@Table(name = "EVENTUSER")
public class EventEntity extends UserEntity {

    public EventEntity(long authorId, String name) {
        super(authorId, name);

    }

    @Column(name = "DateEvent")
    public Date getDateEvent() {
        return getDate("dateEvent");
    }

    public String toString() {
        return "Event{" + super.toString() + "}";
    }
    
  
}
