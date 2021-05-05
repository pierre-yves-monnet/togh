/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.entity;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.togh.entity.ToghUserEntity.ContextAccess;
import com.togh.entity.base.UserEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/* ******************************************************************************** */
/*                                                                                  */
/*  EventSurveyEntity,                                                                      */
/*                                                                                  */
/*  Manage Survey in a event                                                          */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Entity

@Table(name = "EVTSURVEYANSWER")
@EqualsAndHashCode(callSuper=true)
public @Data class EventSurveyAnswerEntity extends UserEntity {

    // User attached to this task (maybe an external user, why not ?
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "whoid")
    private ToghUserEntity whoId;

    @ElementCollection(  fetch = FetchType.EAGER )
    @CollectionTable(name = "EVTSURVEYANSWERCHOICE", 
      joinColumns = {@JoinColumn(name = "surveyid", referencedColumnName = "id")})
    @Fetch(value = FetchMode.SELECT)
    @MapKeyColumn(name = "choice")
    @Column(name = "decision")
    
    private Map<String, Boolean> decision;

    
    /**
     * Get the information as the levelInformation in the event. A OWNER see more than a OBSERVER for example
     * @param levelInformation
     * @return
     */
    @Override
    public Map<String,Object> getMap( ContextAccess contextAccess, Long timezoneOffset) {
        Map<String,Object> resultMap = super.getMap( contextAccess, timezoneOffset );
        
        resultMap.put("decision", decision ==null ? new HashMap<>() : decision);

        // we just return the ID here
        resultMap.put("whoid",whoId==null ? null :  whoId.getId());

        return resultMap;
    }

}
