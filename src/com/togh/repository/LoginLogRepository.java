/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.repository;

import com.togh.entity.LoginLogEntity;
import com.togh.service.LoginService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LoginLogRepository extends JpaRepository<LoginLogEntity, Long> {

  @Query("select loginLog from LoginLogEntity loginLog  where timeSlot=:timeSlot and email=:email and googleId=:googleId and ipAddress=:ipAddress and statusConnection=:statusConnection")
  LoginLogEntity findByTimeSlot(@Param("timeSlot") String timeSlot,
                                @Param("email") String email,
                                @Param("googleId") String googleId,
                                @Param("ipAddress") String ipaddress,
                                @Param("statusConnection") LoginService.LoginStatus statusConnection);


  @Query("select count(LoginLogEntity) from LoginLogEntity where timeSlot=:timeSlot")
  Long countByTimeSlot(@Param("timeSlot") String timeSlot);


  //  status_connection,count(*), sum(number_of_tentatives)
  @Query("select new com.togh.repository.LoginLogStats(timeSlot, statusConnection, count(*), sum(numberOfTentatives) )" +
      " from LoginLogEntity" +
      " where dateCreation >= :dateLimit" +
      " group by statusConnection, timeSlot  order by timeSlot, statusConnection")
  List<LoginLogStats> getStatistics(@Param("dateLimit") LocalDateTime dateLimit);

}