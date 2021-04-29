/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.togh.entity.AdminStatsConnectionEntity;
import com.togh.entity.AdminStatsConnectionEntity.TypeStatsEnum;

public interface AdminStatsConnectionRepository extends JpaRepository<AdminStatsConnectionEntity, Long> {

    @Query("select adminstats from AdminStatsConnectionEntity adminstats where adminstats.yearMonthDay = ?1 and typeStatistique = ?2")
    public AdminStatsConnectionEntity findByDate(String dateToSearch, TypeStatsEnum typeStats);

    @Query(value= "select adminstats from AdminStatsConnectionEntity adminstats order by adminstats.yearMonthDay asc")
    public AdminStatsConnectionEntity getAllDates();

}
