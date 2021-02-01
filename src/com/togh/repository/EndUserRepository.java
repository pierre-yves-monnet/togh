package com.togh.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.togh.entity.ToghUserEntity;

public interface EndUserRepository extends JpaRepository<ToghUserEntity, Long>  {
    
    public ToghUserEntity findById(long id);
    
    public ToghUserEntity findByEmail(String email);
    
    public ToghUserEntity findByConnectionStamp(String connectionStamp );
    
    
    @Query("select p from ToghUserEntity p "
            + "where upper(p.firstName) like concat('%', upper(?1), '%') "
            + " or upper(p.lastName) like concat('%', upper(?2), '%') "
            + " or upper(p.phoneNumber) like concat('%', upper(?3), '%')"
            + " or upper(p.email) like concat('%', upper(?4), '%')"
            + " order by p.firstName, p.lastName, p.phoneNumber, p.email"
            + "")
    List<ToghUserEntity> findByAttributes(String firstName, String lastName, String phoneNumber, String email);
}
