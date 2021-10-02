/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.repository;

import com.togh.entity.ToghUserLostPasswordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ToghUserLostPasswordRepository extends JpaRepository<ToghUserLostPasswordEntity, Long> {

    // public ToghUserEntity findById(long id);
    // @Query("select toghuser from ToghUserEntity toghuser where toghuser.id = ?1")
    // public ToghUserEntity findById(long id);

    @Query("select toghLost from ToghUserLostPasswordEntity toghLost where toghLost.uuid = :uuid")
    public List<ToghUserLostPasswordEntity> findByUUID(@Param("uuid") String uuid);

}
