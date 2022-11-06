/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.service;


import com.togh.engine.logevent.LogEvent;
import com.togh.entity.AdminParameterEntity;
import com.togh.repository.AdminParametersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/* ******************************************************************************** */
/*                                                                                  */
/*  ApiKeyService,                                                                  */
/*                                                                                  */
/* Manage all keys                                                                  */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
@Service
public class AdminParameterService {
    private static final LogEvent eventParameterUpdated = new LogEvent(AdminParameterService.class.getName(), 1, LogEvent.Level.SUCCESS, "Parameter updated", "Parameter uodated with success");
    private static final LogEvent eventParameterError = new LogEvent(AdminParameterService.class.getName(), 2, LogEvent.Level.ERROR, "Parameter not updated",
            "Error during the update",
            "Parameter is not saved",
            "Check error");
    private final static LogEvent eventParameterUnknown = new LogEvent(AdminParameterService.class.getName(), 3, LogEvent.Level.ERROR, "Parameter unknow", "This parameter is unknown", "Parameter not saved", "Check RestAPI");
    private final static LogEvent eventParameterSuccess = LogEvent.getInstanceShortSuccess(AdminParameterService.class.getName(), 4, "Parameter updated");

    @Autowired
    AdminParametersRepository adminParametersRepository;

    /**
     * @return the list of all parameters
     */
    public Map<String, String> getParameters() {
        List<AdminParameterEntity> listAdminParameter = adminParametersRepository.findAll();
        Map<String, String> result = new HashMap<>();
        listAdminParameter.forEach(w -> result.put(w.getName(), w.getValue()));
        return result;
    }

    /**
     * getParameter
     *
     * @param adminParameter parameters to access
     * @return return the value
     */
    public Optional<String> getParameter(AdminParameter adminParameter) {
        List<AdminParameterEntity> listAdminParameter = adminParametersRepository.findAll();

        Optional<AdminParameterEntity> entity = listAdminParameter.stream()
                .filter(w -> w.getName().equalsIgnoreCase(adminParameter.toString()))
                .findFirst();
        return Optional.ofNullable(entity.isPresent() ? entity.get().getValue() : null);
    }

    /**
     * Set the list of parameters
     *
     * @param adminParameters map of admin parameters
     * @return a message with the status
     */
    public AdminParameterServiceStatus setParameters(Map<String, Object> adminParameters) {
        AdminParameterServiceStatus resultStatus = new AdminParameterServiceStatus();
        for (Map.Entry param : adminParameters.entrySet()) {
            Enum adminParam;
            try {
                adminParam = AdminParameter.valueOf(param.getKey().toString().toUpperCase());
            } catch (Exception e) {
                adminParam = null;
            }
            if (adminParam == null) {
                resultStatus.listEvents.add(new LogEvent(eventParameterUnknown, "Key [" + param.getKey().toString() + "]"));
                continue;
            }
            AdminParameterEntity parameterEntity = new AdminParameterEntity();
            parameterEntity.setName(param.getKey().toString());
            parameterEntity.setValue(param.getValue().toString());
            try {
                adminParametersRepository.save(parameterEntity);
            } catch (Exception e) {
                resultStatus.listEvents.add(new LogEvent(eventParameterError, e, "Key [" + param.getKey().toString() + "]"));

            }
        }
        if (resultStatus.listEvents.isEmpty()) {
            resultStatus.listEvents.add(eventParameterSuccess);
        }
        return resultStatus;
    }

    /**
     * Update a parameter
     *
     * @param parameter parameter to update
     * @param value     new value to assign
     * @return status of update
     */
    public List<LogEvent> updateParameter(AdminParameter parameter, String value) {
        List<LogEvent> listLogEvent = new ArrayList<>();

        try {
            AdminParameterEntity adminParameterEntity = adminParametersRepository.findByName(parameter.toString());
            if (adminParameterEntity != null) {

                adminParameterEntity.setValue(value);
                adminParametersRepository.save(adminParameterEntity);
            } else {
                adminParameterEntity = new AdminParameterEntity();
                adminParameterEntity.setName(parameter.toString());
                adminParameterEntity.setValue(value);
                adminParametersRepository.save(adminParameterEntity);
            }
        } catch (Exception e) {
            listLogEvent.add(new LogEvent(eventParameterError, e, "Update/Create [" + parameter + "] value[" + value + "]"));
        }
        if (listLogEvent.isEmpty())
            listLogEvent.add(eventParameterUpdated);
        return listLogEvent;
    }

    /**
     * List of parameters to ask
     */
    public enum AdminParameter {IPADDRESS}

    public class AdminParameterServiceStatus {
        public List<LogEvent> listEvents = new ArrayList<>();

    }
}
