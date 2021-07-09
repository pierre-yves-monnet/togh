/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.togh.entity.ToghUserEntity.ContextAccess;
import com.togh.entity.base.UserEntity;

import lombok.EqualsAndHashCode;

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
    
    public static final String CST_SLABOPERATION_CHATGROUP = "chatgroup";
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
    private List<EventChatEntity> listChat = new ArrayList<>();

    
    public List<EventChatEntity> getListChat() {
        return listChat;
    }
    
    /**
     * Add a chat. The size is limited; if a new chat is added and go over the limit, then the first item is removed.
     * @param chatEntity
     * @param maxChatEntity
     */
    public void addChat( EventChatEntity chatEntity, int maxChatEntity) {
        listChat.add( chatEntity);
        if (listChat.size()>maxChatEntity)
            listChat.remove(0);
    }
    /**
     * Get the information as the levelInformation in the event. A OWNER see more than a OBSERVER for example
     * @param levelInformation
     * @return
     */
    @Override
    public Map<String,Object> getMap( ContextAccess contextAccess, Long timezoneOffset) {
        Map<String,Object> resultMap = super.getMap( contextAccess, timezoneOffset );
        

        resultMap.put("description", description);

        List<Map<String, Object>> listChatMap = new ArrayList<>();
        if (listChat!=null)
            for (EventChatEntity chat : listChat) {
                listChatMap.add(chat.getMap(contextAccess, timezoneOffset));
            }
        resultMap.put( "chatlist", listChatMap);
        
        return resultMap;
    }

}
