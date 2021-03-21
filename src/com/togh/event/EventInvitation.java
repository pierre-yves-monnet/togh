/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.event;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.togh.entity.EventEntity;
import com.togh.entity.ParticipantEntity;
import com.togh.entity.ToghUserEntity;
import com.togh.entity.ParticipantEntity.ParticipantRoleEnum;
import com.togh.entity.ParticipantEntity.StatusEnum;
import com.togh.repository.EventRepository;
import com.togh.service.FactoryService;
import com.togh.service.MonitorService;
import com.togh.service.NotifyService;
import com.togh.service.ToghUserService;
import com.togh.service.EventService.InvitationResult;
import com.togh.service.EventService.InvitationStatus;
import com.togh.service.MonitorService.Chrono;
import com.togh.service.ToghUserService.CreationResult;


/* ******************************************************************************** */
/*                                                                                  */
/*  EventInvitation,                                                                */
/*                                                                                  */
/*  Manage invitation                                                               */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

public class EventInvitation {

    @Autowired
    private FactoryService factoryService;

    @Autowired
    private EventRepository eventRepository;

    EventController eventController;

    protected EventInvitation(EventController eventController) {
        this.eventController = eventController;
    }

    public InvitationResult invite(EventEntity event,
            ToghUserEntity invitedByToghUser,
            List<Long> listUsersId,
            String userInvitedEmail,
            ParticipantRoleEnum role, String message) {
        InvitationResult invitationResult = new InvitationResult();

        MonitorService monitorService = factoryService.getMonitorService();

        Chrono chronoInvitation = monitorService.startOperation("Invitation");
        ToghUserService userService = factoryService.getToghUserService();
        NotifyService notifyService = factoryService.getNotifyService();

        // invitation by the email? 
        if (userInvitedEmail != null && !userInvitedEmail.trim().isEmpty()) {
            ToghUserEntity toghUser = userService.getFromEmail(userInvitedEmail);
            if (toghUser == null) {
                // this is a real new user, register and invite it to join Togh
                CreationResult creationStatus = userService.inviteNewUser(userInvitedEmail, invitedByToghUser, event);
                if (creationStatus.toghUser == null) {
                    invitationResult.status = InvitationStatus.ERRORDURINGCREATIONUSER;
                    // This is an internal message here , cant sent back to the user error information 

                    return invitationResult;
                }
                invitationResult.listThogUserInvited.add(creationStatus.toghUser);
            } else {
                invitationResult.listThogUserInvited.add(toghUser);
            }
        }

        // ---- from the list of ToghUserId

        if (listUsersId != null && !listUsersId.isEmpty()) {
            for (Long userId : listUsersId) {
                // Javascript will pass a Integer or a String (JS doesn not manage correctly large Long number as Integer)
                ToghUserEntity toghUser = null;
                if (userId != null)
                    toghUser = userService.getUserFromId(userId);
                if (toghUser == null) {

                    // caller has supposed to give a valid userId. Stop immediatelly

                    invitationResult.status = InvitationStatus.INVALIDUSERID;
                    // This is an internal message here , cant sent back to the user error information 
                    monitorService.endOperation(chronoInvitation);
                    return invitationResult;
                }
                invitationResult.listThogUserInvited.add(toghUser);

            }
        }

        // check if one users was already a participant ?
        boolean doubleInvitation = false;
        for (ToghUserEntity toghUser : invitationResult.listThogUserInvited) {
            ParticipantEntity participant = this.eventController.getParticipant(toghUser);
            if (participant != null) {
                doubleInvitation = true;
                invitationResult.errorMessage.append(toghUser.getFirstname() + " " + toghUser.getLastName() + ", ");
            } else {
                // send the invitation and register the guy
                notifyService.notifyNewUserInEvent(toghUser, invitedByToghUser, event);
                invitationResult.newParticipants.add(event.addPartipant(toghUser, role, StatusEnum.INVITED));
                invitationResult.okMessage.append(toghUser.getLabel() + ", ");
            }
        }

        eventRepository.save(event);

        // status now
        if (invitationResult.listThogUserInvited.isEmpty())
            invitationResult.status = InvitationStatus.NOUSERSGIVEN;
        else if (doubleInvitation)
            invitationResult.status = InvitationStatus.ALREADYAPARTICIPANT;
        else
            invitationResult.status = InvitationStatus.INVITATIONSENT;

        monitorService.endOperation(chronoInvitation);
        return invitationResult;
    }
}
