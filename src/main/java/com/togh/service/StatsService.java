/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.service;

import com.togh.engine.tool.EngineTool;
import com.togh.entity.AdminStatsUsageEntity;
import com.togh.entity.AdminStatsUsageEntity.TypeStatsEnum;
import com.togh.entity.ToghUserEntity.SubscriptionUserEnum;
import com.togh.repository.AdminStatsUsageRepository;
import com.togh.service.SubscriptionService.LimitReach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;

/* -------------------------------------------------------------------- */
/*                                                                      */
/* StatsService service */
/*                                                                      */
/* -------------------------------------------------------------------- */
@Service
public class StatsService {
  @Autowired
  AdminStatsUsageRepository adminStatsConnectionRepository;


  public void registerLogin() {
    // register the login
    String currentDayString = EngineTool.dateToString(LocalDate.now(ZoneOffset.UTC));
    AdminStatsUsageEntity currentDayEntity = adminStatsConnectionRepository.findByDate(currentDayString, TypeStatsEnum.CONNECTION);
    if (currentDayEntity == null) {
      currentDayEntity = new AdminStatsUsageEntity();
      currentDayEntity.setYearMonthDay(currentDayString);
      currentDayEntity.setValue(0L);
      currentDayEntity.setTypeStatistique(TypeStatsEnum.CONNECTION);
    }
    currentDayEntity.setValue(Long.valueOf(currentDayEntity.value + 1));
    adminStatsConnectionRepository.save(currentDayEntity);

    // a login is an access
    registerAccess();
  }

  public void registerAccess() {
    // register the login
    String currentDayString = EngineTool.dateToString(LocalDate.now(ZoneOffset.UTC));
    AdminStatsUsageEntity currentDayEntity = adminStatsConnectionRepository.findByDate(currentDayString, TypeStatsEnum.ACCESS);
    if (currentDayEntity == null) {
      currentDayEntity = new AdminStatsUsageEntity();
      currentDayEntity.setYearMonthDay(currentDayString);
      currentDayEntity.setValue(0L);
      currentDayEntity.setTypeStatistique(TypeStatsEnum.ACCESS);
    }
    currentDayEntity.setValue(Long.valueOf(currentDayEntity.value + 1));
    adminStatsConnectionRepository.save(currentDayEntity);
  }

  /**
   * Limit Subscription
   */
  public void registerLimitSubcription(SubscriptionUserEnum subscriptionUser, LimitReach limit) {
    // register the login
    String currentDayString = EngineTool.dateToString(LocalDate.now(ZoneOffset.UTC));
    AdminStatsUsageEntity currentDayEntity = adminStatsConnectionRepository.findByDateLimit(currentDayString, TypeStatsEnum.LIMITSUBSCRIPT, subscriptionUser.toString(), limit.toString());
    if (currentDayEntity == null) {
      currentDayEntity = new AdminStatsUsageEntity();
      currentDayEntity.setYearMonthDay(currentDayString);
      currentDayEntity.setValue(0L);
      currentDayEntity.setTypeStatistique(TypeStatsEnum.LIMITSUBSCRIPT);
      currentDayEntity.setSubscriptionUser(subscriptionUser);
      currentDayEntity.setLimitReach(limit);
    }
    currentDayEntity.setValue(Long.valueOf(currentDayEntity.value + 1));
    adminStatsConnectionRepository.save(currentDayEntity);
  }

}
