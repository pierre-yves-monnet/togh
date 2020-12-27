package com.together.data.entity.base;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.json.simple.JSONValue;

public class BaseEntity {
    
    /** 
     * each Entity has a Name and an ID
     */
    private @Id @GeneratedValue(strategy = GenerationType.AUTO) Long id;
     
    
    /**
     * Save all attributes in a Map, then generic function like getMap(), toJson(), fromJson() are possible
     */
    private Map<String,Object> attributes = new HashMap<>();
    
    public BaseEntity( String name ) {
        attributes.put("name", name);
    }
    
      

    /* -------------------------------------------------------------------- */
    /*                                                                      */
    /* Attributes management */
    /*                                                                      */
    /* -------------------------------------------------------------------- */
    public void set(String attributName, Object value) {
        attributes.put( attributName, value );
    }
    public Object get(String attributName) {
        return attributes.get( attributName);
    }
    public String getString( String attributName) {
        return (String) get(attributName);
    }
    public Long getLong( String attributName) {
        return (Long) get(attributName);
    }
    public Date getDate( String attributName) {
        return (Date) get(attributName);
    }
    
    
    /* -------------------------------------------------------------------- */
    /*                                                                      */
    /* Basic attribut */
    /*                                                                      */
    /* -------------------------------------------------------------------- */

    @Column(name="NAME", length=50)
    public String getName() {
        return getString("name");
    }
    
    /**
     * when the object is saved, it will have an unique Id
     * @return
     */
    public Long getId() {
        return id;
    }

    
    /* -------------------------------------------------------------------- */
    /*                                                                      */
    /* Main tool on attribute */
    /*                                                                      */
    /* -------------------------------------------------------------------- */
    public Map<String,Object> getMap() {
        return attributes;
    }
 
    @SuppressWarnings("unchecked")
    public void setJson(String jsonSt) {
        attributes= (Map<String, Object>) JSONValue.parse( jsonSt );
    }
    
    public String getJson() {
        return  JSONValue.toJSONString( attributes );
    }
    
    public static String getListJson( List<?> listBaseEntity  ) {
        List<Map<String,Object>> jsonList= new ArrayList<>();
        for (Object entity  : listBaseEntity) {
            jsonList.add( ((BaseEntity)entity).attributes);
        }
        return  JSONValue.toJSONString( jsonList );
    }
    


}
