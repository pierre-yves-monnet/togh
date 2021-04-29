/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.service;

import java.time.LocalDate;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.togh.engine.tool.EngineTool;
import com.togh.entity.AdminStatsConnectionEntity;
import com.togh.entity.AdminStatsConnectionEntity.TypeStatsEnum;
import com.togh.repository.AdminStatsConnectionRepository;

/* -------------------------------------------------------------------- */
/*                                                                      */
/* StatsService service */
/*                                                                      */
/* -------------------------------------------------------------------- */
@Service
public class StatsService {
    @Autowired
    AdminStatsConnectionRepository adminStatsConnectionRepository;

    
    public void registerLogin() {
        // register the login
        String currentDayString =  EngineTool.dateToString(LocalDate.now(ZoneOffset.UTC));
        AdminStatsConnectionEntity currentDayEntity=  adminStatsConnectionRepository.findByDate( currentDayString,TypeStatsEnum.CONNECTION );
        if (currentDayEntity == null) {
            currentDayEntity=new AdminStatsConnectionEntity();
            currentDayEntity.setYearMonthDay(currentDayString);
            currentDayEntity.setValue(0L);
            currentDayEntity.setTypeStatistique( TypeStatsEnum.CONNECTION);
        }
        currentDayEntity.setValue( Long.valueOf( currentDayEntity.value+1));
        adminStatsConnectionRepository.save(currentDayEntity );

        // a login is an access
        registerAccess();
    }       
        
    public void registerAccess() {
        // register the login
        String currentDayString =  EngineTool.dateToString(LocalDate.now(ZoneOffset.UTC));
        AdminStatsConnectionEntity currentDayEntity=  adminStatsConnectionRepository.findByDate( currentDayString,TypeStatsEnum.ACCESS );
        if (currentDayEntity == null) {
            currentDayEntity=new AdminStatsConnectionEntity();
            currentDayEntity.setYearMonthDay(currentDayString);
            currentDayEntity.setValue(0L);
            currentDayEntity.setTypeStatistique( TypeStatsEnum.ACCESS);
        }
        currentDayEntity.setValue( Long.valueOf( currentDayEntity.value+1));
        adminStatsConnectionRepository.save(currentDayEntity );
    }
    
}
