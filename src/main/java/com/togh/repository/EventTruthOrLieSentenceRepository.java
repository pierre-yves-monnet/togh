/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.repository;

import com.togh.entity.EventGameTruthOrLieSentenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/* ******************************************************************************** */
/*                                                                                  */
/* EventTruthLieRepository */
/*                                                                                  */
/* Save TruthLieEntity */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

public interface EventTruthOrLieSentenceRepository extends JpaRepository<EventGameTruthOrLieSentenceEntity, Long> {

}

