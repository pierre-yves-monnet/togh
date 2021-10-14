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
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
@EqualsAndHashCode(callSuper = true)


public @Data
class EventGroupChatEntity extends UserEntity {

    public static final String CST_SLABOPERATION_GROUPCHATLIST = "groupchatlist";

    public static final String CST_DEFAULT_GROUP = "general";

    // choice : list of "code/ proposition"
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SELECT)
    @BatchSize(size = 100)
    @JoinColumn(name = "groupchatid")
    @OrderBy("id")
    private final List<EventChatEntity> listChat = new ArrayList<>();
    // name is part of the baseEntity
    @Column(name = "description", length = 400)
    private String description;


    public List<EventChatEntity> getListChat() {
        return listChat;
    }

    public void removeChat(int position) {
        listChat.remove(position);
    }

    /**
     * Add a chat.
     *
     * @param chatEntity
     */
    public void addChat(EventChatEntity chatEntity) {
        listChat.add(chatEntity);
    }

}
