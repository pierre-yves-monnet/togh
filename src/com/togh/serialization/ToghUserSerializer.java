/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.serialization;

import com.togh.engine.tool.EngineTool;
import com.togh.entity.ToghUserEntity;
import com.togh.entity.base.BaseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class ToghUserSerializer extends BaseSerializer {


    @Override
    public Class getEntityClass() {
        return ToghUserEntity.class;
    }

    /**
     * Get the information as the levelInformation in the event. A OWNER see more than a OBSERVER for example
     *
     * @param baseEntity        toghUser to serialize
     * @param contextAccess     Context of the access
     * @param timeZoneOffset    time Zone offset of the browser
     * @param factorySerializer
     * @return Map to describe the user
     */
    @Override
    public Map<String, Object> getMap(BaseEntity baseEntity, ToghUserEntity.ContextAccess contextAccess, Long timeZoneOffset, FactorySerializer factorySerializer) {
        ToghUserEntity toghUserEntity = (ToghUserEntity) baseEntity;
        Map<String, Object> resultMap = super.getBasicMap(toghUserEntity, contextAccess, timeZoneOffset);

        StringBuilder label = new StringBuilder();
        StringBuilder longlabel = new StringBuilder();

        if (toghUserEntity.getName() != null) {
            label.append(toghUserEntity.getName());
            longlabel.append(toghUserEntity.getName());
        }

        resultMap.put("tagid", System.currentTimeMillis() % 10000);
        resultMap.put("name", toghUserEntity.getName());
        resultMap.put("firstName", toghUserEntity.getFirstName());
        resultMap.put("typePicture", toghUserEntity.getTypePicture());
        resultMap.put("picture", toghUserEntity.getPicture());
        resultMap.put("source", toghUserEntity.getSource().toString().toLowerCase());


        resultMap.put("statusUser", toghUserEntity.getStatusUser() == null ? "" : toghUserEntity.getStatusUser().toString());

        // if the context is SECRET, the last name is not visible
        if (contextAccess != ToghUserEntity.ContextAccess.SECRETACCESS)
            resultMap.put("lastName", toghUserEntity.getLastName());

        if (isVisible(toghUserEntity, toghUserEntity.getEmailVisibility(), contextAccess)) {
            resultMap.put("email", toghUserEntity.getEmail());
            if (toghUserEntity.getEmail() != null && toghUserEntity.getEmail().trim().length() > 0) {
                // the label is the email only if there is no label at this moment
                if (label.toString().trim().length() == 0)
                    label.append(" (" + toghUserEntity.getEmail() + ")");
                longlabel.append(" (" + toghUserEntity.getEmail() + ")");
            }
        } else
            resultMap.put("email", "*********");

        if (isVisible(toghUserEntity, toghUserEntity.getPhoneNumberVisibility(), contextAccess)) {
            resultMap.put("phoneNumber", toghUserEntity.getPhoneNumber());
            if (toghUserEntity.getPhoneNumber() != null) {
                if (label.length() == 0)
                    label.append(" " + toghUserEntity.getPhoneNumber());
                longlabel.append(" " + toghUserEntity.getPhoneNumber());
            }
        } else
            resultMap.put("phoneNumber", "*********");

        resultMap.put("label", label.toString());
        resultMap.put("longlabel", longlabel.toString());

        if (contextAccess == ToghUserEntity.ContextAccess.MYPROFILE || contextAccess == ToghUserEntity.ContextAccess.ADMIN) {
            resultMap.put("subscriptionuser", toghUserEntity.getSubscriptionUser().toString());
            resultMap.put("showTipsUser", toghUserEntity.getShowTipsUser());

        }
        if (contextAccess == ToghUserEntity.ContextAccess.ADMIN) {
            resultMap.put("privilegeuser", toghUserEntity.getPrivilegeUser().toString());
            resultMap.put("connectiontime", EngineTool.dateToString(toghUserEntity.getConnectionTime()));
            if (toghUserEntity.getConnectionTime() != null) {
                LocalDateTime connectionTimeLocal = toghUserEntity.getConnectionTime().minusMinutes(timeZoneOffset);
                resultMap.put("connectiontimest", EngineTool.dateToHumanString(connectionTimeLocal));
            }
            resultMap.put("connectionlastactivity", EngineTool.dateToString(toghUserEntity.getConnectionLastActivity()));
            if (toghUserEntity.getConnectionLastActivity() != null) {
                LocalDateTime connectionLastActivityLocal = toghUserEntity.getConnectionLastActivity().minusMinutes(timeZoneOffset);
                resultMap.put("connectionlastactivityst", EngineTool.dateToHumanString(connectionLastActivityLocal));
            }
            resultMap.put("connected", toghUserEntity.getConnectionStamp() == null ? "OFFLINE" : "ONLINE");
        }
        return resultMap;
    }

    private boolean isVisible(ToghUserEntity toghUserEntity, ToghUserEntity.VisibilityEnum visibility, ToghUserEntity.ContextAccess userAccess) {
        // first rule : admin, return true
        if (userAccess == ToghUserEntity.ContextAccess.ADMIN)
            return true;
        // second rule : secret : never.
        if (userAccess == ToghUserEntity.ContextAccess.SECRETACCESS)
            return false;
        // then depends on the visibility and the policy
        if (toghUserEntity.getEmailVisibility() == ToghUserEntity.VisibilityEnum.ALWAYS)
            return true;
        // it's visible only for accepted user in the event
        return (userAccess == ToghUserEntity.ContextAccess.FRIENDACCESS && (visibility == ToghUserEntity.VisibilityEnum.LIMITEDEVENT || visibility == ToghUserEntity.VisibilityEnum.ALWAYBUTSEARCH));
        // in all other case, refuse
    }
}
