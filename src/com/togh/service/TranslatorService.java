/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

/* -------------------------------------------------------------------- */
/*                                                                      */
/* TranslatorService */
/*                                                                      */
/*
 * Server need to translate information (email, texto...)
 * This service are in charge of translation.
 */
/* -------------------------------------------------------------------- */

@Service
public class TranslatorService {

    /**
     * first key is the language, second the dictionary
     */
    private static Map<String, Map<String, String>> dictionary = new HashMap<>();

    public enum Sentence {
        FROM, TO, PARTICIPANTS, INVITED_TOGH_EVENT,DEAR,YOU_ARE_INVITED_BY,
        TO_JOIN_A,TOGH_EVENT,TOGH_EVENT_EXPLANATION,THE_EVENT_WE_PROPOSE_TO_JOIN,
        REGISTER_AND_JOIN_THIS_EVENT, TO_JOIN_EXPLANATION,TO_ACCESS_TOGH,
        YOU_LOST_YOUR_PASSWORD,CLICK_TO_RESET_PASSWORD,RESET_MY_PASSWORD,TOGH_RESET_PASSWORD 
        
    }

    // --------------------------------------------------------------
    // 
    // Initialisation: populate dictionnary
    // 
    // --------------------------------------------------------------
    @PostConstruct
    public void init() {
        populateDefaultDictionary();
    }

    public void populateDictionary(Map<String, Map<String, String>> dictionaryUpdate) {
        dictionary.putAll(dictionaryUpdate);
    }

    public void populateDefaultDictionary() {
        Map<String, String> dicoEnglish = new HashMap<>();
        dicoEnglish.put(Sentence.FROM.toString(), "From");
        dicoEnglish.put(Sentence.TO.toString(), "To");
        dicoEnglish.put(Sentence.PARTICIPANTS.toString(),"Participants");
        dicoEnglish.put(Sentence.INVITED_TOGH_EVENT.toString(), "You are invited in a Togh Event!");
        dicoEnglish.put(Sentence.DEAR.toString(),"Dear");
        dicoEnglish.put(Sentence.YOU_ARE_INVITED_BY.toString(), "You are invited by");
        dicoEnglish.put(Sentence.TO_JOIN_A.toString(), "To join a");
        dicoEnglish.put(Sentence.TOGH_EVENT.toString(), "Togh Event");
        dicoEnglish.put(Sentence.TOGH_EVENT_EXPLANATION.toString(), "In this event, you will share participants, messages, and a lot more information!");
        dicoEnglish.put(Sentence.THE_EVENT_WE_PROPOSE_TO_JOIN.toString(), "The event %s invites you to join is");
        
        dicoEnglish.put(Sentence.REGISTER_AND_JOIN_THIS_EVENT.toString(), "Register and join this event");
        dicoEnglish.put(Sentence.TO_JOIN_EXPLANATION.toString(), "To join, click the button. Register yourself with our email address, and welcome to Togh!");
        dicoEnglish.put(Sentence.TO_ACCESS_TOGH.toString(), "You can access the Togh application via this link to have more information:");
        
        dicoEnglish.put(Sentence.YOU_LOST_YOUR_PASSWORD.toString(),"We are sorry to heard that you lost your password");
        dicoEnglish.put(Sentence.CLICK_TO_RESET_PASSWORD.toString(), "No worry, click on this link to reset it");
        dicoEnglish.put(Sentence.RESET_MY_PASSWORD.toString(), "Reset my password");
        dicoEnglish.put(Sentence.TOGH_RESET_PASSWORD.toString(), "Togh reset password");

        
        dictionary.put("en", dicoEnglish);
    }

    /**
     * First implementation : read in memory
     * 
     * @param message
     * @param lang
     * @return
     */
    public String getDictionarySentence(Sentence sentence, String lang) {
        if (dictionary.containsKey(lang)) {
            if (dictionary.get(lang).containsKey(sentence.toString())) {
                return dictionary.get(lang).get(sentence.toString());
            }
        }
        return sentence.toString();
    }
}
