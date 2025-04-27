/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.repository;

import com.togh.entity.EventGameTruthOrLieVoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/* ******************************************************************************** */
/*                                                                                  */
/* EventTruthLieRepository */
/*                                                                                  */
/* Save TruthLieEntity */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

public interface EventTruthOrLieVoteRepository extends JpaRepository<EventGameTruthOrLieVoteEntity, Long> {

}
