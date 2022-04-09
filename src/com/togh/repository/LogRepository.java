package com.togh.repository;


import com.togh.entity.LogEntity;
import com.togh.entity.LoginLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface LogRepository extends JpaRepository<LogEntity, Long> {

    @Query("select log from LogEntity log  where :logDateStart<=logeveventdate and logeventdate<:logDateEnd order by logeveventdate")
    LoginLogEntity findByTimeSlot(@Param("logDateStart") LocalDateTime logDateStart,
                                  @Param("logDateEnd") LocalDateTime logDateEnd);

}
