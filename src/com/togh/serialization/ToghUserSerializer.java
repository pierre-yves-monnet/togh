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
import com.togh.eventgrantor.update.FactoryUpdateGrantor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class ToghUserSerializer extends BaseSerializer {


    public static final String JSON_LONG_LABEL = "longLabel";
    public static final String JSON_FIRST_NAME = "firstName";
    public static final String JSON_STATUS_USER = "statusUser";

    @Override
    public Class getEntityClass() {
        return ToghUserEntity.class;
    }

    /**
     * Get the information as the levelInformation in the event. A OWNER see more than a OBSERVER for example
     *
     * @param baseEntity           toghUser to serialize
     * @param serializerOptions    Serializer options
     * @param factorySerializer    factory to access all serializer
     * @param factoryUpdateGrantor factory to access Update Grantor
     * @return Map to describe the user
     */
    @Override
    public Map<String, Object> getMap(BaseEntity baseEntity,
                                      BaseEntity parentEntity,
                                      SerializerOptions serializerOptions,
                                      FactorySerializer factorySerializer,
                                      FactoryUpdateGrantor factoryUpdateGrantor) {
        ToghUserEntity toghUserEntity = (ToghUserEntity) baseEntity;
        Map<String, Object> resultMap = super.getBasicMap(toghUserEntity, serializerOptions);

        StringBuilder label = new StringBuilder();
        StringBuilder longLabel = new StringBuilder();

        if (toghUserEntity.getName() != null) {
            label.append(toghUserEntity.getName());
            longLabel.append(toghUserEntity.getName());
        }

        resultMap.put("tagid", System.currentTimeMillis() % 10000);
        resultMap.put("name", toghUserEntity.getName());
        resultMap.put(JSON_FIRST_NAME, toghUserEntity.getFirstName());
        resultMap.put("typePicture", toghUserEntity.getTypePicture());
        resultMap.put("picture", toghUserEntity.getPicture());
        resultMap.put("source", toghUserEntity.getSource().toString().toLowerCase());


        resultMap.put(JSON_STATUS_USER, toghUserEntity.getStatusUser() == null ? "" : toghUserEntity.getStatusUser().toString());

        // if the context is SECRET, the last name is not visible
        if (serializerOptions.getContextAccess().equals(SerializerOptions.ContextAccess.EVENTACCESS)) {
            // this is in the case of an event. Is the event is a Secret?
            if (serializerOptions.getEventAccessGrantor().isOtherParticipantsVisible())
                resultMap.put("lastName", toghUserEntity.getLastName());
        }

        if (isVisible(toghUserEntity, toghUserEntity.getEmailVisibility(), serializerOptions)) {
            resultMap.put("email", toghUserEntity.getEmail());
            if (toghUserEntity.getEmail() != null && toghUserEntity.getEmail().trim().length() > 0) {
                // the label is the email only if there is no label at this moment
                if (label.toString().trim().length() == 0)
                    label.append(encapsulate("(", toghUserEntity.getEmail(), ")"));
                longLabel.append(encapsulate(" (", toghUserEntity.getEmail(), ")"));
            }
        } else
            resultMap.put("email", "*********");

        if (isVisible(toghUserEntity, toghUserEntity.getPhoneNumberVisibility(), serializerOptions)) {
            resultMap.put("phoneNumber", toghUserEntity.getPhoneNumber());
            if (toghUserEntity.getPhoneNumber() != null) {
                if (label.length() == 0)
                    label.append(encapsulate(" ", toghUserEntity.getPhoneNumber(), " )"));
                longLabel.append(encapsulate(" ", toghUserEntity.getPhoneNumber(), " "));
            }
        } else
            resultMap.put("phoneNumber", "*********");

        resultMap.put("label", label.toString());
        resultMap.put(JSON_LONG_LABEL, longLabel.toString());

        if (serializerOptions.getContextAccess().equals(SerializerOptions.ContextAccess.MYPROFILE)
                || serializerOptions.getContextAccess().equals(SerializerOptions.ContextAccess.ADMIN)) {
            resultMap.put("subscriptionuser", toghUserEntity.getSubscriptionUser().toString());
            resultMap.put("showTipsUser", toghUserEntity.getShowTipsUser());

        }
        if (serializerOptions.getContextAccess().equals(SerializerOptions.ContextAccess.ADMIN)) {
            resultMap.put("privilegeuser", toghUserEntity.getPrivilegeUser().toString());
            resultMap.put("connectiontime", EngineTool.dateToString(toghUserEntity.getConnectionTime()));
            if (toghUserEntity.getConnectionTime() != null) {
                LocalDateTime connectionTimeLocal = toghUserEntity.getConnectionTime().minusMinutes(serializerOptions.getTimezoneOffset());
                resultMap.put("connectiontimest", EngineTool.dateToHumanString(connectionTimeLocal));
            }
            resultMap.put("connectionlastactivity", EngineTool.dateToString(toghUserEntity.getConnectionLastActivity()));
            if (toghUserEntity.getConnectionLastActivity() != null) {
                LocalDateTime connectionLastActivityLocal = toghUserEntity.getConnectionLastActivity().minusMinutes(serializerOptions.getTimezoneOffset());
                resultMap.put("connectionlastactivityst", EngineTool.dateToHumanString(connectionLastActivityLocal));
            }
            resultMap.put("connected", toghUserEntity.getConnectionStamp() == null ? "OFFLINE" : "ONLINE");
        }
        return resultMap;
    }

    /**
     * isVisible? If the toghUser can show the information, according the visibility of the attribut?
     *
     * @param toghUser         user to verify the information
     * @param visibility       the visibility on the attribute
     * @param serializeOptions options and context to access the information
     * @return true if the attribut is visible
     */
    private boolean isVisible(ToghUserEntity toghUser, ToghUserEntity.VisibilityEnum visibility, SerializerOptions serializeOptions) {
        // first rule : admin, return true
        if (serializeOptions.getContextAccess().equals(SerializerOptions.ContextAccess.ADMIN))
            return true;
        // second rule : secret : never.
        if (serializeOptions.isHighProtectionUser())
            return false;
        // then depends on the visibility and the policy
        if (toghUser.getEmailVisibility() == ToghUserEntity.VisibilityEnum.ALWAYS)
            return true;
        // it's visible only for accepted user in the event
        return ((serializeOptions.getEventAccessGrantor() != null)
                && (visibility == ToghUserEntity.VisibilityEnum.LIMITEDEVENT || visibility == ToghUserEntity.VisibilityEnum.ALWAYBUTSEARCH)
                && (serializeOptions.getEventAccessGrantor().isOtherParticipantsVisible()));
    }

    /**
     * Encapsulate a string with something. Example: encapsulate( "Hello", "(") return "(Hello)"
     *
     * @param encapsulateInfoBegin information begin
     * @param content              information to encapsulate
     * @param encapsulateInfoEnd   information end
     * @return a String with the encapsulated value
     */
    private String encapsulate(String encapsulateInfoBegin, String content, String encapsulateInfoEnd) {
        return encapsulateInfoBegin + content + encapsulateInfoEnd;
    }
}
