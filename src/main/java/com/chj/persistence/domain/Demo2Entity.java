package com.chj.persistence.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Demo2Entity {
    @Id @GeneratedValue
    private long id;
   
    private String name;
 
    public long getId() {
       return id;
    }
 
    public void setId(long id) {
       this.id = id;
    }
 
    public String getName() {
       return name;
    }
 
    public void setName(String name) {
       this.name = name;
    }
 
    @Override
    public String toString() {
       return"Demo [id=" + id + ", name=" + name + "]";
    }
}
