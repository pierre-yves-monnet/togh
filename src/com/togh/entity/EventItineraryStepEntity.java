/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.entity;

import java.time.LocalDate;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.togh.engine.tool.EngineTool;
import com.togh.entity.ToghUserEntity.ContextAccess;
import com.togh.entity.base.EventBaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/* ******************************************************************************** */
/*                                                                                  */
/*  EventItineraryStep,                                                                      */
/*                                                                                  */
/*  Manage task in a event                                                          */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Entity

@Table(name = "EVTITINERARYSTEP")
@EqualsAndHashCode(callSuper=true)
public @Data class EventItineraryStepEntity extends EventBaseEntity {

    public static final String CST_SLABOPERATION_ITINERARYSTEPLIST = "itinerarysteplist";

    public enum CategoryEnum {
        POI,BEGIN,END,SHOPPING,AIRPORT,BUS,TRAIN,BOAT,NIGHT,VISIT,RESTAURANT,ENTERTAINMENT
    }
    @Column(name = "category", length=15)
    @Enumerated(EnumType.STRING)    
    private CategoryEnum category;

    // name is part of the baseEntity
    @Column( name="description", length=400)
    private String description;

    
    @Column( name="datestep", nullable = false)
    private LocalDate dateStep;

    @Column( name="rownumber", nullable = false)
    private Integer rownumber;

    
    // format is HH:MM
    @Column(name = "visittime", length=5)
    private String visitTime;
    
    
    @Column(name = "durationtime", length=5)
    private String durationTime;
    
    
    @Column(name = "geoaddress", length=300)
    private String geoaddress;

    @Column(name = "geolat")
    private Double geolat;

    
    @Column(name = "geolng")
    private Double geolng;

    
    @Column(name = "website", length=300)
    private String website;

    // Expense attached to this task 
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "expenseid")
    private EventExpenseEntity expense;

    @Override
    public boolean acceptExpense() {
        return true;
    }
   
    
    /**
     * The dateStep is manipulate by the interface, not by the user.
     */
    public boolean isAbsoluteLocalDate(String attributName ) {
        if ("dateStep".equalsIgnoreCase(attributName))
            return true;
        return false;
    }

    /**
     * Get the information as the levelInformation in the event. A OWNER see more than a OBSERVER for example
     * @param levelInformation
     * @return
     */
    @Override
    public Map<String,Object> getMap( ContextAccess contextAccess, Long timezoneOffset) {
        Map<String,Object> resultMap = super.getMap( contextAccess, timezoneOffset );
        
        resultMap.put("dateStep", EngineTool.dateToString( dateStep));
        resultMap.put("rownumber", rownumber);

        resultMap.put("category",category==null ? null : category.toString());
        resultMap.put("visitTime", visitTime);
        resultMap.put("durationTime", durationTime);
        resultMap.put("description", description);
        resultMap.put("geoaddress", geoaddress);
        resultMap.put("geolat", geolat);
        resultMap.put("geolng", geolng);
        resultMap.put("website", website);
        
        
        // Here we attached directly the expense information
        resultMap.put("expense", expense==null ? null : expense.getMap(contextAccess, timezoneOffset));
        

        return resultMap;
    }
}
