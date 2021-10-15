/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.entity;

import com.togh.entity.base.UserEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;

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
@EqualsAndHashCode(callSuper = true)
public @Data
class EventTaskEntity extends UserEntity {

    public static final String CST_SLABOPERATION_TASKLIST = "tasklist";
    @Column(name = "status", length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskStatusEnum status;
    @Column(name = "datestarttask")
    private LocalDateTime dateStartTask;
    @Column(name = "dateendtask")
    private LocalDateTime dateEndTask;
    // name is part of the baseEntity
    @Column(name = "description", length = 400)
    private String description;
    // User attached to this task (maybe an external user, why not ?
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "whoid")
    private ToghUserEntity whoId;

    public enum TaskStatusEnum {
        PLANNED, ACTIVE, DONE, CANCEL
    }


}
