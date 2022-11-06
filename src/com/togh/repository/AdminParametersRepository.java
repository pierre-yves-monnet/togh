/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.repository;

import com.togh.entity.AdminParameterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AdminParametersRepository extends JpaRepository<AdminParameterEntity, Long> {


    @Query("SELECT e FROM AdminParameterEntity e where e.name = :name order by e.name")
    AdminParameterEntity findByName(String name);

    @Query("SELECT e FROM AdminParameterEntity e order by e.name")
    List<AdminParameterEntity> findAll();
}
