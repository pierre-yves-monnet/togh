/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.repository;

import com.togh.entity.APIKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiKeyEntityRepository extends JpaRepository<APIKeyEntity, Long> {

    APIKeyEntity findByName(String codeApi);

}
