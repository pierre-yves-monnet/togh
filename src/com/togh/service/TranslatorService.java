/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.service;

import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

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
    private final static Logger logger = Logger.getLogger(TranslatorService.class.getName());
    private final static String LOGGER_HEADER = TranslatorService.class.getName() + ": ";


    /**
     * first key is the language, second the dictionary
     */
    private final static Map<String, Map<String, String>> dictionary = new HashMap<>();

    // --------------------------------------------------------------
    //
    // Initialisation: populate dictionary
    //
    // --------------------------------------------------------------
    @PostConstruct
    public void init() {
        populateDefaultDictionary();
    }

    public void populateDefaultDictionary() {
        Properties props = null;
        for (DictionaryEnum dico : DictionaryEnum.values()) {
            try {
                String dicoSt = dico.toString().toLowerCase();

                props = PropertiesLoaderUtils.loadAllProperties("dictionary_" + dicoSt + ".properties");

                Map<String, String> dicoLanguage = new HashMap<>();
                props.forEach((key, value) -> dicoLanguage.put((String) key, (String) value));
                dictionary.put(dicoSt, dicoLanguage);
            } catch (IOException e) {
                logger.severe("Can't read properties file " + e);
            }
        }
    }

    ;

    /**
     * First implementation : read in memory
     *
     * @param sentence sentence to get
     * @param language language to get the sentence
     * @return the sentence in the target
     */
    public String getDictionarySentence(Sentence sentence, String language) {
        // only in English at this moment
        if (!dictionary.containsKey(language)) {
            language = "en";
        }
        return dictionary.get(language).getOrDefault(sentence.toString().toLowerCase(), sentence.toString());
    }

    public void populateDictionary(Map<String, Map<String, String>> dictionaryUpdate) {
        dictionary.putAll(dictionaryUpdate);
    }

    public enum Sentence {
        FROM, TO, PARTICIPANTS, INVITED_TOGH_EVENT, DEAR, YOU_ARE_INVITED_BY,
        TO_JOIN_A, TOGH_EVENT, TOGH_EVENT_EXPLANATION, THE_EVENT_WE_PROPOSE_TO_JOIN,
        REGISTER_AND_JOIN_THIS_EVENT, TO_JOIN_EXPLANATION, TO_ACCESS_TOGH,
        YOU_LOST_YOUR_PASSWORD, CLICK_TO_RESET_PASSWORD, RESET_MY_PASSWORD, TOGH_RESET_PASSWORD

    }

    public enum DictionaryEnum {EN, FR}
}
