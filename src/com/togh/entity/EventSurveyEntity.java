/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;
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

@Table(name = "EVTSURVEY")
@EqualsAndHashCode(callSuper=true)
public @Data class EventSurveyEntity extends UserEntity {

    public static final String CST_SLABOPERATION_CHOICELIST = "choicelist";
    public static final String CST_SLABOPERATION_ANSWERLIST = "answerlist";

    public enum SurveyStatusEnum {
        INPREPAR, OPEN,CLOSE
    }
    @Column(name = "status", length=10, nullable= false)
    @Enumerated(EnumType.STRING)    
    private SurveyStatusEnum status;

    // name is part of the baseEntity
    @Column( name="description", length=400)
    private String description;
  
    // choice : list of "code/ proposition"
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SELECT)
    @BatchSize(size=100)
    @JoinColumn(name = "surveyid")
    @OrderBy("id")
    private List<EventSurveyChoiceEntity> choicelist = new ArrayList<>();

    
  
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SELECT)
    @Column(name = "answer", length=100)
    @JoinColumn(name = "surveyid")
    @OrderBy("id")
    private List<EventSurveyAnswerEntity> answerlist = new ArrayList<>();
 
    
    /**
     * Get the information as the levelInformation in the event. A OWNER see more than a OBSERVER for example
     * @param levelInformation
     * @return
     */
    @Override
    public Map<String,Object> getMap( ContextAccess contextAccess, Long timezoneOffset) {
        Map<String,Object> resultMap = super.getMap( contextAccess, timezoneOffset );
        

        resultMap.put("status",status==null ? null : status.toString());
        resultMap.put("description", description);

        List<Map<String, Object>> listChoiceMap = new ArrayList<>();
        if (choicelist!=null)
            for (EventSurveyChoiceEntity choice : choicelist) {
                listChoiceMap.add(choice.getMap(contextAccess, timezoneOffset));
            }
        resultMap.put( CST_SLABOPERATION_CHOICELIST, listChoiceMap);
        
        List<Map<String, Object>> listAnswerMap = new ArrayList<>();
        if (answerlist!=null)
            for (EventSurveyAnswerEntity answer : answerlist) {
                listAnswerMap.add(answer.getMap(contextAccess, timezoneOffset));
            }
        resultMap.put( CST_SLABOPERATION_ANSWERLIST, listAnswerMap);

      
        return resultMap;
    }

}
