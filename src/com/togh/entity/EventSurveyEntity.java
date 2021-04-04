/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.togh.engine.tool.EngineTool;
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

@Table(name = "EVTSURVEY")
@EqualsAndHashCode(callSuper=true)
public @Data class EventSurveyEntity extends UserEntity {

    public enum SurveyStatusEnum {
        INPREPAR, OPEN,CLOSE
    }
    @Column(name = "status", length=10, nullable= false)
    @Enumerated(EnumType.STRING)    
    private SurveyStatusEnum status;

    // name is part of the baseEntity
    @Column( name="description", length=400)
    private String description;
  
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "whoid")
    private ToghUserEntity whoId;

    @ElementCollection( fetch = FetchType.EAGER)
    @CollectionTable(name = "EVTSURVEYCHOICE", joinColumns = @JoinColumn(name = "id"))
    @Fetch(value = FetchMode.SUBSELECT)
    @Column(name = "choice", length=100)
    private List<String> choicelist;
  
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    @JoinColumn(name = "surveyid")
    @OrderBy("id")
    private List<EventSurveyAnswerEntity> answerlist = new ArrayList<>();
 
    
    /**
     * Get the information as the levelInformation in the event. A OWNER see more than a OBSERVER for example
     * @param levelInformation
     * @return
     */
    @Override
    public Map<String,Object> getMap( ContextAccess contextAccess) {
        Map<String,Object> resultMap = super.getMap( contextAccess );
        

        resultMap.put("status",status==null ? null : status.toString());
        resultMap.put("description", description);
        resultMap.put("choice", choicelist);

      
        return resultMap;
    }

}
