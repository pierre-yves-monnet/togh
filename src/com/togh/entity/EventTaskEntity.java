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
import com.togh.entity.base.UserEntity;

import lombok.Data;

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
public @Data class EventTaskEntity extends UserEntity {

    public enum TaskStatusEnum {
        PLANNED, ACTIVE, DONE, CANCEL
    }
    @Column(name = "status", length=10)
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

    /*
    public TaskStatusEnum getStatus() {
        return status;
    }
    public void setStatus(TaskStatusEnum status) {
        this.status = status;
    }

    
    public LocalDateTime getDateStartTask() {
        return dateStartTask;
    }
    public void setDateStartTask(LocalDateTime dateStartTask) {
        this.dateStartTask = dateStartTask;
    }

    
    public LocalDateTime getDateEndTask() {
        return dateEndTask;
    }
    public void setDateEndTask(LocalDateTime dateEndTask) {
        this.dateEndTask = dateEndTask;
    }

    
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    
    public ToghUserEntity getWhoId() {
        return whoId;
    }
    public void setWhoId(ToghUserEntity whoId) {
        this.whoId = whoId;
    }
*/
    /**
     * Get the information as the levelInformation in the event. A OWNER see more than a OBSERVER for example
     * @param levelInformation
     * @return
     */
    @Override
    public Map<String,Object> getMap( ContextAccess contextAccess) {
        Map<String,Object> resultMap = super.getMap( contextAccess );
        

        resultMap.put("status",status==null ? null : status.toString());
        resultMap.put("datestarttask", EngineTool.dateToString( dateStartTask));
        resultMap.put("dateendtask", EngineTool.dateToString( dateEndTask));
        resultMap.put("description", description);

        // we just return the ID here
        resultMap.put("whoid",whoId==null ? null :  whoId.getId());

        return resultMap;
    }

}
