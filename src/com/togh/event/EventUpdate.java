/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.togh.engine.logevent.LogEvent;
import com.togh.engine.logevent.LogEvent.Level;
import com.togh.engine.tool.EngineTool;
import com.togh.entity.EventEntity;
import com.togh.entity.EventTaskEntity;
import com.togh.entity.EventEntity.DatePolicyEnum;
import com.togh.entity.base.BaseEntity;
import com.togh.repository.EventRepository;
import com.togh.service.EventService;
import com.togh.service.FactoryService;
import com.togh.service.EventService.EventOperationResult;


/* ******************************************************************************** */
/*                                                                                  */
/*  EventUpdate,                                                                    */
/*                                                                                  */
/*  Manage update of event, from a SlabRecord                                       */
/*  A SlabRecord is a simple 'operation' to update part of the event, like the name */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

public class EventUpdate {
    
    @Autowired
    private FactoryService factoryService;

    @Autowired
    private EventRepository eventRepository;

    private static final LogEvent eventInvalidUpdateOperation = new LogEvent(EventUpdate.class.getName(), 1, Level.APPLICATIONERROR, "Invalid operation", "This operation failed", "Operation can't be done", "Check error");
    EventController eventController;
    
    private enum SlabOperation {UPDATE, ADD }
    private class Slab {
        public SlabOperation operation;
        public String attributName;
        public Object attributValue;
        public String localisation;
        public String typedata;
        
        protected Slab( Map<String,Object> record ) 
        {
            operation= SlabOperation.valueOf( (String) record.get("operation"));
            attributName = (String) record.get( "name");
            attributValue = record.get( "value");
            localisation = (String) record.get( "localisation");
            typedata=(String) record.get("typedata");
        }
    }
    
    protected EventUpdate(EventController eventController) {
        this.eventController = eventController;
    }
    public EventOperationResult update(List<Map<String,Object>>  listSlab) {
        EventEntity event = this.eventController.getEvent();
        EventOperationResult eventOperationResult = new EventOperationResult();
        for (Map<String,Object> recordSlab : listSlab) {
            try {
                Slab slab = new Slab( recordSlab);
                if ( SlabOperation.UPDATE.equals( slab.operation)) {
                    updateOperation( event, slab,eventOperationResult );
                } else if (SlabOperation.ADD.equals( slab.operation)) {
                    addOperation( event, slab, eventOperationResult );
                }
            }catch(Exception e) {
                eventOperationResult.listEvents.add( new LogEvent(eventInvalidUpdateOperation, e, recordSlab.get("operation")+":"+recordSlab.get("name")));
            }
        }
        if (! listSlab.isEmpty())
            event.touch();
       
        return eventOperationResult;
    }
    
    private void addOperation(  EventEntity event, Slab slab, EventOperationResult eventOperationResult) {
        BaseEntity child=null;
    
        if (slab.attributName.equals("tasklist")) {
            child = event.addTask();
        }
        if (child!=null) {
            updateEntityOperation( child, slab, eventOperationResult);
            eventOperationResult.listChildEntity.add( child );
        }
        return;
    }
    /**
     * Update the event Eventity with the slab
     * @param event
     * @param slab
     * @return
     */
    private void updateOperation(  EventEntity event, Slab slab, EventOperationResult eventOperationResult) {
        if (slab.localisation.isEmpty())
             updateEntityOperation(event, slab, eventOperationResult);
        

    }
    
    
    /**
     * update an entity
     * @param event
     * @param slab
     * @return
     */
    private void updateEntityOperation( BaseEntity event, Slab slab,EventOperationResult eventOperationResult ) {
        
        
        String methodName = "get"+slab.attributName.substring(0,1).toUpperCase()+slab.attributName.substring(1);
        Object value=null;
        if (slab.attributValue != null) {
            for (Method method : event.getClass().getMethods())
            {
                if (method.getName().equals( methodName ))
                {
                    Class returnType = method.getReturnType();
                    if (returnType.equals( Double.class)) {
                        value = Double.valueOf( slab.attributValue.toString());        
                    }
                    else if (returnType.equals( Long.class)) {
                        value = Long.valueOf( slab.attributValue.toString());        
                    }
                    else if (returnType.equals( LocalDateTime.class)) {
                        value =  EngineTool.stringToDate(slab.attributValue.toString());;        
                    }
                    else if (returnType.equals( String.class)) {
                        value = slab.attributValue.toString();
                    }
                    else if (returnType.equals( Enum.class)) {
                        value = Enum.valueOf( returnType,slab.attributValue.toString()); 
                    }
                    else  {
                        value = slab.attributValue;
                    }
                }
            }
        }
        /*if (slab.attributValue !=null && "datePolicyEnum".equals( slab.typedata) ) {
            slab.attributValue = DatePolicyEnum.valueOf( slab.attributValue.toString());
        }
        */
        try {
        if (slab.localisation.isEmpty()) {
            PropertyUtils.setSimpleProperty(event, slab.attributName, value);

        }

        } catch (Exception e) {
            eventOperationResult.listEvents.add( new LogEvent(eventInvalidUpdateOperation, e, slab.operation.toString()+":"+slab.attributName+" <="
                    + (slab.attributValue==null ? "null" : "("+slab.attributValue.getClass().getName()+") "+ slab.attributValue)));
        }
        return;
    }
}
