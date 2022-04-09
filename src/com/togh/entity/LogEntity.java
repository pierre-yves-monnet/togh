/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.entity;

import com.togh.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

/* ******************************************************************************** */
/*                                                                                  */
/* Log : record any MAININFO and ERROR level                                        */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Entity
@Table(name = "LOG")
@EqualsAndHashCode(callSuper = false)
public @Data
class LogEntity extends BaseEntity {
    @Column(name = "logeventdate", length = 20)
    private LocalDateTime logEventDate;

    @Column(name = "logeventlevel", length = 20)
    private String logEventLevel;


    @Column(name = "logeventpackagename", length = 100)
    private String logEventPackageName;

    @Column(name = "logeventnumber")
    private int logEventNumber;

    @Column(name = "logeventtitle", length = 100)
    private String logEventTitle;

    @Column(name = "logeventparameter", length = 400)
    private String logEventParameters; // optional parameters

    @Column(name = "username", length = 100)
    private String userName;

}
