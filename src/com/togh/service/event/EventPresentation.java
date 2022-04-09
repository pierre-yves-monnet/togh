/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.service.event;

import com.togh.entity.EventEntity;
import com.togh.entity.EventEntity.DatePolicyEnum;
import com.togh.entity.ParticipantEntity;
import com.togh.entity.ToghUserEntity;
import com.togh.serialization.FactorySerializer;
import com.togh.serialization.SerializerOptions;
import com.togh.serialization.ToghUserSerializer;
import com.togh.service.FactoryService;
import com.togh.service.TranslatorService.Sentence;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;



/* ******************************************************************************** */
/*                                                                                  */
/*  EventPresentation,                                                              */
/*                                                                                  */
/*  Create presentation for an event, to send in an Email, etc                       */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

public class EventPresentation {

    private static final String NBSP = "&nbsp;";


    private final EventController eventController;
    private final FactoryService factoryService;

    protected EventPresentation(EventController eventController, FactoryService factoryService) {
        this.eventController = eventController;
        this.factoryService = factoryService;
    }

    /**
     * Return a nice HTML presentation
     *
     * @param eventPresentationAttribut attribut to build the presentation
     * @param toghUserEntity            user who send the presentation
     * @param factorySerializer         to access the toghSerializer to get the user label
     * @return
     */
    public String getHtmlPresentation(EventPresentationAttribut eventPresentationAttribut,
                                      ToghUserEntity toghUserEntity,
                                      FactorySerializer factorySerializer) {
        EventEntity event = eventController.getEvent();
        StringBuilder result = new StringBuilder();


        result.append("<center>");
        result.append("<table style=\"border:3px solid;border-color:#1f78b4;border-collapse: collapse;margin: 20px 5px 20px 5px;\" >");
        result.append("  <tr style=\"border:1px solid;border-color:#1f78b4;\">");
        result.append("    <td style=\"padding: 10px 10px 10px 10px;font-weight: bold;font-size: 20px;\">" + event.getName() + "</td>");
        result.append("    <td style=\"border:1px solid;border-color:#1f78b4;padding: 10px 10px 10px 10px;font-style: italic;font-size: 12px;\">");
        if (event.getDatePolicy() == DatePolicyEnum.ONEDATE) {
            result.append(getHumanDate(event.getDateEvent(), event, toghUserEntity, true));
        } else {
            result.append(factoryService.getTranslatorService().getDictionarySentence(Sentence.FROM, toghUserEntity.getLanguage()));
            result.append(NBSP);
            result.append(getHumanDate(event.getDateStartEvent(), event, toghUserEntity, false));
            result.append(NBSP);
            result.append(factoryService.getTranslatorService().getDictionarySentence(Sentence.TO, toghUserEntity.getLanguage()));
            result.append(NBSP);
            result.append(getHumanDate(event.getDateEndEvent(), event, toghUserEntity, true));
            result.append(NBSP);
        }

        result.append("</td></tr>");
        result.append("<tr style=\"border:1px solid;border-color:#1f78b4\">");
        result.append("  <td colspan=\"2\" style=\"padding: 10px 10px 10px 10px;\">" + event.getDescription() + "</td>");
        result.append("</tr><tr style=\"border:1px solid;border-color:#1f78b4;\">");
        result.append("  <td colspan=\"2\" style=\"padding: 10px 10px 10px 10px;\"><i>");
        result.append(factoryService.getTranslatorService().getDictionarySentence(Sentence.PARTICIPANTS, toghUserEntity.getLanguage()));
        result.append(":</i><br>");

        ToghUserSerializer toghUserSerializer = (ToghUserSerializer) factorySerializer.getFromClass(ToghUserEntity.class);
        SerializerOptions serializeOptions = new SerializerOptions(toghUserEntity, 0L, SerializerOptions.ContextAccess.EVENTACCESS);

        for (ParticipantEntity participant : event.getParticipantList()) {
            ToghUserEntity userParticipant = participant.getUser();
            if (userParticipant != null)
                result.append(NBSP + NBSP + toghUserSerializer.getUserLabel(userParticipant, serializeOptions) + "<br>");
        }
        result.append("  </td>");
        result.append("</tr>");

        if (eventPresentationAttribut.bannerMessage != null) {
            result.append("<tr style=\"border:1px solid;border-color:#1f78b4;\">");
            result.append("  <td colspan=\"2\" style=\"padding: 10px 10px 10px 10px;\">");
            result.append("     <center>");
            result.append(eventPresentationAttribut.bannerMessage);
            // <a href="http://localhost:3000/togh/registerUser"
            //     style="text-decoration: none;color: white;">Register and join this event</a>
            result.append("      </center>");
            result.append("</td></tr>");
        }

        if (eventPresentationAttribut.bannerAction != null) {
            result.append("<tr style=\"border:1px solid;border-color:#1f78b4;\">");
            result.append("  <td colspan=\"2\" style=\"padding: 10px 10px 10px 10px;background-color: #337ab7;border-color: #2e6da4;color: #ffffff;\">");
            result.append("     <center>");
            result.append(eventPresentationAttribut.bannerAction);
            // <a href="http://localhost:3000/togh/registerUser"
            //     style="text-decoration: none;color: white;">Register and join this event</a>
            result.append("      </center>");
            result.append("</td></tr>");
        }
        result.append("</table>");
        result.append("</center>");


        return result.toString();
    }


    /**
     * @param date
     * @param event
     * @param toghUserEntity
     * @param displayTimeZone
     * @return
     */
    public String getHumanDate(LocalDateTime date, EventEntity event, ToghUserEntity toghUserEntity, boolean displayTimeZone) {
        if (date == null)
            return "";
        ZonedDateTime dateUtc = date.atZone(ZoneId.of("UTC"));
        // translate to the time user
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy MMM dd HH:mm:ss");
        return format.format(dateUtc);

    }
}
