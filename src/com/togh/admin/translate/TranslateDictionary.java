package com.togh.admin.translate;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

/* ******************************************************************************** */
/*                                                                                  */
/* TranslateDictionnary, */
/*                                                                                  */
/* Pilote the translation of all directory */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.togh.admin.translate.ToghDictionary.SentenceItem;
import com.togh.admin.translate.TranslatorGoogle.TranslateSentenceResult;
import com.togh.engine.chrono.ChronoSet;
import com.togh.engine.chrono.Chronometer;
import com.togh.engine.logevent.LogEvent;
import com.togh.engine.logevent.LogEvent.Level;
import com.togh.engine.logevent.LogEventFactory;

/**
 * https://code.google.com/archive/p/java-google-translate-text-to-speech/
 */

@Configuration
// @ P ropertySource("classpath:application.properties")
public class TranslateDictionary {

    @Autowired
   private TranslatorGoogle translatorGoogle;
    
    private static final LogEvent eventDictionaryOperationError = new LogEvent(TranslateDictionary.class.getName(), 1, Level.ERROR, "During Dictionary operation", "Operation on dictionnary will failed", "Dictionary is empty", "Check Exception");
    private static final LogEvent eventDictionaryPathNotDefined = new LogEvent(TranslateDictionary.class.getName(), 2, Level.ERROR, "Path to access dictionnary is not setted in the configuration file", "Operation on dictionnaries are not possible", "Dictionaries will not change", "Check configuration file");

    private Logger logger = Logger.getLogger(TranslateDictionary.class.getName());
    private final static String logHeader = "com.togh.TranslateDictionary";

    public class LanguageResult {

        public String name;
        public int nbMissingSentences = 0;
        public int nbTranslatedSentences = 0;
        public int nbTooMuchSentences = 0;
    }

    public class TranslateResult {

        public int nbLanguages = 0;
        public int nbSentences = 0;
        public int nbTranslationPerformed = 0;
        public List<LanguageResult> listLanguages = new ArrayList<>();
        public List<LogEvent> listEvents = new ArrayList<>();
        public Map<String,Object> chronometers;
    }

    // dictionary.LangPath=D:\dev\git\togh\npm\src\lang
    @Value("${dictionary.lang-path}")
    private String propertyDictionaryPath;

    // dictionary.ExtractPath=D:\dev\git\togh\npm\lang
    @Value("${dictionary.extract-path}")
    private String propertyExtractPath;

    public TranslateResult complete() {
        return operationOnDictionary(true);
    }

    public TranslateResult check() {
        return operationOnDictionary(false);
    }

    /**
     * @return
     */
    private TranslateResult operationOnDictionary(boolean translate) {
        TranslateResult translateResult = new TranslateResult();
        // load all dictionnary
        
        
        try {
            
            
            if (propertyExtractPath==null || propertyDictionaryPath == null)
            {
                translateResult.listEvents.add( eventDictionaryPathNotDefined );
                return translateResult;
            }
            
        File extractFolder = new File( propertyExtractPath );
        
        logger.info("Current Directory [" + extractFolder.getAbsolutePath() + "]");
        ToghExtractDictionary toghReference = new ToghExtractDictionary(extractFolder, "en");

        translateResult.listEvents.addAll( toghReference.read() );
        //---- prepare the translator
        ChronoSet chronoSet = new ChronoSet();
        Chronometer chronoTranslate = chronoSet.getChronometer("translate");
        Chronometer chronoInitialisation = chronoSet.getChronometer("initialisation");

        chronoInitialisation.start();
        translateResult.listEvents.addAll( translatorGoogle.initialisation() );
        chronoInitialisation.stop();
        
        // detect the languages
        File languageFolder= new File( propertyDictionaryPath );
        List<String> detectedLanguages = detectLanguages(languageFolder);
        int countTranslation=1;

        for (String language : detectedLanguages) {
            ToghDictionary toghLanguage = new ToghDictionary(languageFolder, language);
            translateResult.listEvents.addAll( toghLanguage.read() );
            
            LanguageResult languageResult = new LanguageResult();
            languageResult.name = language;
            translateResult.listLanguages.add( languageResult);

            // first, check all missing sentences            
            if ( toghReference.getDictionary() !=null)
                for (SentenceItem sentenceEntry : toghReference.getDictionary() ) {
                    if (toghLanguage.exist(sentenceEntry.key)) {
                        // override the original sentence
                        toghLanguage.setSentence(sentenceEntry.key, toghLanguage.getTranslation( sentenceEntry.key), sentenceEntry.translation);
                    }
                    else {
                        languageResult.nbMissingSentences++;
                        if (!translate)
                            continue;
                        if ("en".equals(language)) {
                            // no translation needed here
                            languageResult.nbTranslatedSentences++;
                            toghLanguage.setSentence(sentenceEntry.key, sentenceEntry.translation, sentenceEntry.translation);
                        } else {
                            if (countTranslation>1000)
                                break;
                            
                            countTranslation++;                            
                            // traduction needed
                            chronoTranslate.start();
                            TranslateSentenceResult translation = translatorGoogle.translateSentence( sentenceEntry.translation, "en", language);
                            chronoTranslate.stop();
                            
                            translateResult.listEvents.addAll(translation.listEvents);
                            if (!LogEventFactory.isError(translation.listEvents)) {
                                languageResult.nbTranslatedSentences++;
                                toghLanguage.setSentence(sentenceEntry.key, translation.getTranslation(), sentenceEntry.translation);
                            }
                        }
                    }
                }
            if (toghLanguage.isModified()) {
                translateResult.listEvents.addAll(toghLanguage.write());
            }

            // second, maybe too many sentences ?
            for (SentenceItem sentenceEntry : toghLanguage.dictionary.values()) {
                if (!toghReference.exist(sentenceEntry.key)) {
                    languageResult.nbTooMuchSentences++;
                }
            }

        } // end language
        chronoSet.logChronometer();        
        translateResult.chronometers = chronoSet.getMap();
        
        Collections.sort(translateResult.listLanguages, new Comparator<LanguageResult>() 
        {
          public int compare(LanguageResult s1, LanguageResult s2)
          {
            return s1.name.compareTo(s2.name);
          }
        });

        
        }catch(Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionDetails = sw.toString();

            logger.severe(logHeader + "During operationDictionary "+e+" at "+exceptionDetails);
            translateResult.listEvents.add( new LogEvent(eventDictionaryOperationError,e, e.getMessage()) );
        }
        return translateResult;
    }

    /**
     * Detect all languages
     * 
     * @param directoryLanguage
     * @return
     */
    private List<String> detectLanguages(File directoryLanguage) {
        List<String> listLanguages = new ArrayList<>();
        for (File fileInDirectory : directoryLanguage.listFiles()) {
            if (fileInDirectory.isFile() && fileInDirectory.getName().endsWith(".json")) {
                listLanguages.add(fileInDirectory.getName().substring(0, fileInDirectory.getName().length() - ".json".length()));
            }
        }
        return listLanguages;
    }
}
