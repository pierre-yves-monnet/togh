/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.entity;

import com.togh.entity.ToghUserEntity.ContextAccess;
import com.togh.entity.base.UserEntity;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* ******************************************************************************** */
/*                                                                                  */
/*  EventGroupChat                                                                  */
/*                                                                                  */
/*  A event manage multiple group chat. A group chat has                            */
/*      - a name,                                                                   */
/*      - a list of participants                                                    */
/*      - a reference to an external app (What's app, ...                           */
/*      - and a list of messages                                                    */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Entity

@Table(name = "EVTGROUPCHAT")
@EqualsAndHashCode(callSuper=true)



public class EventGroupChatEntity  extends UserEntity {

    public static final String CST_SLABOPERATION_GROUPCHATLIST = "groupchatlist";

    public static final String CST_DEFAULT_GROUP = "general";

    // name is part of the baseEntity
    @Column( name="description", length=400)
    private String description;
  
    // choice : list of "code/ proposition"
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SELECT)
    @BatchSize(size=100)
    @JoinColumn(name = "groupchatid")
    @OrderBy("id")
    private final List<EventChatEntity> listChat = new ArrayList<>();

    
    public List<EventChatEntity> getListChat() {
        return listChat;
    }
    public void removeChat( int position) {
         listChat.remove(position);
    }
    /**
     * Add a chat. 
     * @param chatEntity
     */
    public void addChat( EventChatEntity chatEntity) {
        listChat.add( chatEntity);
    }
    /**
     * Get the information as the levelInformation in the event. A OWNER see more than a OBSERVER for example
     * @param contextAccess the Context Access
     * @param timeZoneOffset the time zone offset of the browser
     * @return
     */
    @Override
    public Map<String,Object> getMap( ContextAccess contextAccess, Long timeZoneOffset) {
        Map<String,Object> resultMap = super.getMap( contextAccess, timeZoneOffset );
        

        resultMap.put("description", description);

        List<Map<String, Object>> listChatMap = new ArrayList<>();
        for (EventChatEntity chat : listChat) {
            listChatMap.add(chat.getMap(contextAccess, timeZoneOffset));
        }
        resultMap.put( "chatlist", listChatMap);
        
        return resultMap;
    }

}
