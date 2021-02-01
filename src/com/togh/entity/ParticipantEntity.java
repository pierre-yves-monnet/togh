package com.togh.entity;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.togh.entity.EventEntity.TypeEventEnum;
import com.togh.entity.ToghUserEntity.ContextAccess;
import com.togh.entity.base.UserEntity;

/* ******************************************************************************** */
/*                                                                                  */
/* Participant to an event */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Entity

@Table(name = "EVTPARTICIPANT")
public class ParticipantEntity extends UserEntity {

    public enum ParticipantRoleEnum {
        OWNER, ORGANIZER, PARTICIPANT, OBSERVER, WAITINGCONFIRMATION, EXTERNAL
    }

    @Column(name = "role", length = 10, nullable = false)
    private ParticipantRoleEnum role;

    // User attached to this participant
    @OneToOne(fetch = FetchType.EAGER)
    private ToghUserEntity user;

    // @Column(name = "useridtemp")
    // private Long userIdtemp;

    public enum StatusEnum {
        INVITED, ACTIF, LEFT
    }

    @Column(name = "status", length = 10, nullable = false)
    private StatusEnum status;

    public ParticipantRoleEnum getRole() {
        return role;
    }

    public void setRole(ParticipantRoleEnum role) {
        this.role = role;
    }

    public ToghUserEntity getUser() {
        return user;
    }

    public void setUser(ToghUserEntity endUser) {
        this.user = endUser;
    }

    public Long getUserId() {
        return user.getId();
    }
    /*
     * public void setUserId(Long userId) {
     * this.userIdtemp = userId;
     * }
     */

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

   
    /**
     * Get the information as the levelInformation in the event. A OWNER see more than a OBSERVER for example
     * @param levelInformation
     * @return
     */
    public Map<String,Object> getMap( ContextAccess contextAccess) {
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("role", role==null ? null : role.toString());
        resultMap.put("user", user.getMap(contextAccess ));
        resultMap.put("id", getId());
        resultMap.put("status", status==null ? null : status.toString());

        return resultMap;
    }
}
