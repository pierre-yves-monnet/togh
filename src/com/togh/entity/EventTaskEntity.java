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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.togh.entity.ParticipantEntity.ParticipantRoleEnum;
import com.togh.entity.base.UserEntity;

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
public class EventTaskEntity extends UserEntity {

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
   
    // Participant attached to this task
    @ManyToOne(fetch = FetchType.EAGER)
    private ToghUserEntity who;

    
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

    
    public ToghUserEntity getWho() {
        return who;
    }

    
    public void setWho(ToghUserEntity who) {
        this.who = who;
    }


}
