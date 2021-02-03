package com.togh.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.togh.entity.ToghUserEntity;
import com.togh.entity.ToghUserEntity.SourceUserEnum;

public interface EndUserRepository extends JpaRepository<ToghUserEntity, Long>  {
    
    public ToghUserEntity findById(long id);
    
    public ToghUserEntity findByEmail(String email);
    
    public ToghUserEntity findByConnectionStamp(String connectionStamp );
    
    
    @Query("select p from ToghUserEntity p "
            + "where "
            + "( ?1 = '' or upper(p.firstName) like concat('%', upper(?1), '%')) "
            + " and (?2 = '' or upper(p.lastName) like concat('%', upper(?2), '%')) "
            + " and (?3 = '' or upper(p.phoneNumber) like concat('%', upper(?3), '%'))"
            + " and (?4 = '' or upper(p.email) like concat('%', upper(?4), '%'))"
            + " order by p.firstName, p.lastName, p.phoneNumber, p.email"
            + "")
    List<ToghUserEntity> findByAttributes(String firstName, String lastName, String phoneNumber, String email);
    
    @Query("select p from ToghUserEntity p "
            + "where "
            + "( ?1 = '' or upper(p.firstName) like concat('%', upper(?1), '%')) "
            + " and (?2 = '' or upper(p.lastName) like concat('%', upper(?2), '%')) "
            + " and (?3 = '' or upper(p.phoneNumber) like concat('%', upper(?3), '%'))"
            + " and (?4 = '' or upper(p.email) like concat('%', upper(?4), '%'))"
            + " and p.searchable=true "
            + " and (p.source != 'INVITED' )"
            + " order by p.firstName, p.lastName, p.phoneNumber, p.email"
            + "")
    List<ToghUserEntity> findAvailableUsers(String firstName, String lastName, String phoneNumber, String email);

}
