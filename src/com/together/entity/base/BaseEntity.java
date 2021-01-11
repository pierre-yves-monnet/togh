package com.together.entity.base;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Inheritance
public abstract class BaseEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;  

    private String name;
    
    private LocalDateTime datecreation;

    private LocalDateTime datemodification;
         
    public BaseEntity( String name ) {
        this.name= name;
        setDatecreation( LocalDateTime.now(ZoneOffset.UTC));

    }
    public BaseEntity() {
        
    }
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDateTime getDatecreation() {
		return datecreation;
	}

	public void setDatecreation(LocalDateTime datecreation) {
		this.datecreation = datecreation;
	}

	public LocalDateTime getDatemodification() {
		return datemodification;
	}

	public void setDatemodification(LocalDateTime datemodification) {
		this.datemodification = datemodification;
	}
}
