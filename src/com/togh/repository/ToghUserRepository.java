package com.togh.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.togh.entity.ToghUserEntity;
import com.togh.entity.ToghUserEntity.SourceUserEnum;

public interface ToghUserRepository extends JpaRepository<ToghUserEntity, Long>  {
    
    public ToghUserEntity findById(long id);
    
    public ToghUserEntity findByEmail(String email);
    
    public ToghUserEntity findByConnectionStamp(String connectionStamp );
    
    
    @Query("select toghuser from ToghUserEntity toghuser "
            + "where "
            + "( ?1 = '' or upper(toghuser.firstName) like concat('%', upper(?1), '%')) "
            + " and (?2 = '' or upper(toghuser.lastName) like concat('%', upper(?2), '%')) "
            + " and (?3 = '' or upper(toghuser.phoneNumber) like concat('%', upper(?3), '%'))"
            + " and (?4 = '' or upper(toghuser.email) like concat('%', upper(?4), '%'))"
            + " order by toghuser.firstName, toghuser.lastName, toghuser.phoneNumber, toghuser.email"
            + "")
    List<ToghUserEntity> findByAttributes(String firstName, String lastName, String phoneNumber, String email, Pageable pageable);
    

    /*
     * Search users 
     * - who are searchable
     * - who are confirmed  
     */
    // Pageable : https://www.baeldung.com/spring-data-jpa-query
    @Query(value="select toghuser from ToghUserEntity toghuser "
            + "where "
            + "( :firstName = '' or upper(toghuser.firstName) like concat('%', upper( :firstName ), '%')) "
            + " and (:lastName = '' or upper(toghuser.lastName) like concat('%', upper( :lastName ), '%')) "
            + " and (:phoneNumber = '' or upper(toghuser.phoneNumber) like concat('%', upper( :phoneNumber ), '%'))"
            + " and (:email = '' or upper(toghuser.email) like concat('%', upper( :email ), '%'))"
            + " and (toghuser.searchable=true) "
            + " and (toghuser.source != 'INVITED' )"
            + " order by toghuser.firstName, toghuser.lastName, toghuser.phoneNumber, toghuser.email",
           // + "\n-- #pageable\n",
            countQuery = "select toghuser from ToghUserEntity toghuser "
                    + " where "
                    + " ( :firstName = '' or upper(toghuser.firstName) like concat('%', upper(:firstName), '%')) "
                    + "  and (:lastName = '' or upper(toghuser.lastName) like concat('%', upper(:lastName), '%')) "
                    + "  and (:phoneNumber = '' or upper(toghuser.phoneNumber) like concat('%', upper(:phoneNumber), '%'))"
                    + "  and (:email = '' or upper(toghuser.email) like concat('%', upper(:email), '%'))"
                    + "  and (toghuser.searchable=true) "
                    + "  and (toghuser.source != 'INVITED' )"
                    + "  order by toghuser.firstName, toghuser.lastName, toghuser.phoneNumber, toghuser.email")
    List<ToghUserEntity> findPublicUsers(@Param("firstName") String firstName, @Param("lastName") String lastName, @Param("phoneNumber") String phoneNumber, @Param("email") String email, Pageable pageable);

    @Query(value="select count(toghuser) from ToghUserEntity toghuser "
            + "where "
            + "( :firstName = '' or upper(toghuser.firstName) like concat('%', upper( :firstName ), '%')) "
            + " and (:lastName = '' or upper(toghuser.lastName) like concat('%', upper( :lastName ), '%')) "
            + " and (:phoneNumber = '' or upper(toghuser.phoneNumber) like concat('%', upper( :phoneNumber ), '%'))"
            + " and (:email = '' or upper(toghuser.email) like concat('%', upper( :email ), '%'))"
            + " and toghuser.searchable=true "
            + " and (toghuser.source != 'INVITED' )"
            )
    Long countPublicUsers(@Param("firstName") String firstName, @Param("lastName") String lastName, @Param("phoneNumber") String phoneNumber, @Param("email") String email);
    
    
    
    /**
     * Search user not already registered in an event
     * select e from Employee e
            where e not in (select epar from Employee epar, CertificateOrder c  join c.participants  p
                            where c.persistenceId = :certificateid 
                            and p.employee = epar)
     * @param firstName
     * @param lastName
     * @param phoneNumber
     * @param email
     * @param eventId
     * @param pageable
     * @return
     */
    // + " and user not in (select pauser from EventEntity e, ParticipantEntity pa, ToghUserEntity pauser where e.id = :eventId and e.participants pa and pa.user_id = pauser) "

    @Query(value="select toghuser from ToghUserEntity toghuser "
            + "where "
            + "( :firstName = '' or upper(toghuser.firstName) like concat('%', upper( :firstName ), '%')) "
            + " and (:lastName = '' or upper(toghuser.lastName) like concat('%', upper( :lastName ), '%')) "
            + " and (:phoneNumber = '' or upper(toghuser.phoneNumber) like concat('%', upper( :phoneNumber ), '%'))"
            + " and (:email = '' or upper(toghuser.email) like concat('%', upper( :email ), '%'))"
            + " and toghuser.searchable=true "
            + " and (toghuser.source != 'INVITED' )"
            + " and (toghuser.id > :eventId or toghuser.id <= :eventId)"
            + " order by toghuser.firstName, toghuser.lastName, toghuser.phoneNumber, toghuser.email",
           // + "\n-- #pageable\n",
            countQuery = "select count( toghuser ) from ToghUserEntity toghuser "
                    + "where "
                    + "( :firstName = '' or upper(toghuser.firstName) like concat('%', upper( :firstName ), '%')) "
                    + " and (:lastName = '' or upper(toghuser.lastName) like concat('%', upper( :lastName ), '%')) "
                    + " and (:phoneNumber = '' or upper(toghuser.phoneNumber) like concat('%', upper( :phoneNumber ), '%'))"
                    + " and (:email = '' or upper(toghuser.email) like concat('%', upper( :email ), '%'))"
                    + " and toghuser.searchable=true "
                    + " and (toghuser.id > :eventId or toghuser.id <= :eventId)"
                    + " and (toghuser.source != 'INVITED' )"
            )
                    List<ToghUserEntity> findPublicUsersOutEvent(@Param("firstName") String firstName, @Param("lastName") String lastName, @Param("phoneNumber") String phoneNumber,
            @Param("email") String email, 
            @Param("eventId") Long eventId,
            Pageable pageable);
    
    
    @Query(value= "select count( toghuser ) from ToghUserEntity toghuser "
                    + "where "
                    + "( :firstName = '' or upper(toghuser.firstName) like concat('%', upper( :firstName ), '%')) "
                    + " and (:lastName = '' or upper(toghuser.lastName) like concat('%', upper( :lastName ), '%')) "
                    + " and (:phoneNumber = '' or upper(toghuser.phoneNumber) like concat('%', upper( :phoneNumber ), '%'))"
                    + " and (:email = '' or upper(toghuser.email) like concat('%', upper( :email ), '%'))"
                    + " and toghuser.searchable=true "
                    + " and (toghuser.source != 'INVITED' )"
                    + " and (toghuser.id > :eventId or toghuser.id <=:eventId)"
            )
    Long countPublicUsersOutEvent(@Param("firstName") String firstName, @Param("lastName") String lastName, 
            @Param("phoneNumber") String phoneNumber, 
            @Param("email") String email,
            @Param("eventId") Long eventId);
 
}
