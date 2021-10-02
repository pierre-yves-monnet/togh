/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.entity;

import com.togh.engine.tool.EngineTool;
import com.togh.entity.ToghUserEntity.ContextAccess;
import com.togh.entity.base.UserEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

/* ******************************************************************************** */
/*                                                                                  */
/*  EventTask,                                                                      */
/*                                                                                  */
/*  Manage task in a event                                                          */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Entity

@Table(name = "EVTTASK")
@EqualsAndHashCode(callSuper=true)
public @Data class EventTaskEntity extends UserEntity {

    public static final String CST_SLABOPERATION_TASKLIST = "tasklist";

    public enum TaskStatusEnum {
        PLANNED, ACTIVE, DONE, CANCEL
    }
    @Column(name = "status", length=10, nullable= false)
    @Enumerated(EnumType.STRING)    
    private TaskStatusEnum status;

    
    @Column(name = "datestarttask")
    private LocalDateTime dateStartTask;
    @Column(name = "dateendtask")
    private LocalDateTime dateEndTask;
    
    // name is part of the baseEntity
    @Column( name="description", length=400)
    private String description;
   
    // User attached to this task (maybe an external user, why not ?
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "whoid")
    private ToghUserEntity whoId;

    
    /**
     * Get the information as the levelInformation in the event. A OWNER see more than a OBSERVER for example
     * @param levelInformation
     * @return
     */
    @Override
    public Map<String,Object> getMap( ContextAccess contextAccess, Long timezoneOffset) {
        Map<String,Object> resultMap = super.getMap( contextAccess, timezoneOffset );
        

        resultMap.put("status",status==null ? null : status.toString());
        resultMap.put("datestarttask", EngineTool.dateToString( dateStartTask));
        resultMap.put("dateendtask", EngineTool.dateToString( dateEndTask));
        resultMap.put("description", description);

        // we just return the ID here
        resultMap.put("whoid",whoId==null ? null :  whoId.getId());

        return resultMap;
    }

}
