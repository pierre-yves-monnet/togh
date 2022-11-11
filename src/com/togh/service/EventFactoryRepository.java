/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.service;

import com.togh.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/* ******************************************************************************** */
/*                                                                                  */
/* Acccess all repository, */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Service
public class EventFactoryRepository {

  @Autowired
  public EventRepository eventRepository;

  @Autowired
  public EventItineraryStepRepository eventItineraryStepRepository;

  @Autowired
  public EventTaskRepository eventTaskRepository;

  @Autowired
  public EventChatRepository eventChatRepository;

  @Autowired
  public EventGroupChatRepository eventGroupChatRepository;

  @Autowired
  public EventShoppingListRepository eventShoppingListRepository;

  @Autowired
  public EventSurveyRepository eventSurveyRepository;

  @Autowired
  public EventSurveyAnswerRepository surveyAnswerRepository;

  @Autowired
  public EventSurveyChoiceRepository surveyChoiceRepository;

  @Autowired
  public EventGameRepository eventGameRepository;

  @Autowired
  public EventTruthOrLieRepository eventTruthOrLieRepository;

  @Autowired
  public EventTruthOrLieSentenceRepository eventTruthOrLieSentenceRepository;

  @Autowired
  public EventTruthOrLieVoteRepository eventTruthOrLieVoteRepository;

}
