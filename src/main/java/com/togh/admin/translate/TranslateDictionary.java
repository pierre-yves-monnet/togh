/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.admin.translate;

import com.togh.admin.translate.ToghDictionary.SentenceItem;
import com.togh.admin.translate.TranslatorGoogle.TranslateSentenceResult;
import com.togh.engine.chrono.ChronoSet;
import com.togh.engine.chrono.Chronometer;
import com.togh.engine.logevent.LogEvent;
import com.togh.engine.logevent.LogEvent.Level;
import com.togh.engine.logevent.LogEventFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * https://code.google.com/archive/p/java-google-translate-text-to-speech/
 */
/* ******************************************************************************** */
/*                                                                                  */
/*  TranslateDictionary,                                                                 */
/*                                                                                  */
/*  Translate missing sentences                         */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Configuration
// @ P ropertySource("classpath:application.properties")
public class TranslateDictionary {

  private static final LogEvent eventDictionaryOperationError = new LogEvent(TranslateDictionary.class.getName(), 1, Level.ERROR, "During Dictionary operation", "Operation on dictionnary will failed", "Dictionary is empty", "Check Exception");
  private static final LogEvent eventDictionaryPathNotDefined = new LogEvent(TranslateDictionary.class.getName(), 2, Level.ERROR, "Path to access dictionnary is not setted in the configuration file", "Operation on dictionnaries are not possible", "Dictionaries will not change", "Check configuration file");
  private static final LogEvent eventDictionaryTranslationSuccess = new LogEvent(TranslateDictionary.class.getName(), 3, Level.SUCCESS, "Translation success", "Translation done with success");
  private static final String LOG_HEADER = TranslateDictionary.class.getName() + ":";
  private final Logger logger = Logger.getLogger(TranslateDictionary.class.getName());
  @Autowired
  private TranslatorGoogle translatorGoogle;
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
   * @return translation status
   */
  private TranslateResult operationOnDictionary(boolean translate) {
    TranslateResult translateResult = new TranslateResult();
    // load all dictionary

    try {

      if (propertyExtractPath == null || propertyDictionaryPath == null) {
        translateResult.listEvents.add(eventDictionaryPathNotDefined);
        return translateResult;
      }

      File extractFolder = new File(propertyExtractPath);

      logger.info("Current Directory [" + extractFolder.getAbsolutePath() + "]");
      ToghExtractDictionary toghReference = new ToghExtractDictionary(extractFolder, "en");

      translateResult.listEvents.addAll(toghReference.read());
      //---- prepare the translator
      ChronoSet chronoSet = new ChronoSet();
      Chronometer chronoTranslate = chronoSet.getChronometer("translate");
      Chronometer chronoInitialisation = chronoSet.getChronometer("initialisation");

      chronoInitialisation.start();
      translateResult.listEvents.addAll(translatorGoogle.initialisation());
      chronoInitialisation.stop();

      // detect the languages
      File languageFolder = new File(propertyDictionaryPath);
      List<String> detectedLanguages = detectLanguages(languageFolder);
      int countTranslation = 1;

      for (String language : detectedLanguages) {
        ToghDictionary toghLanguage = new ToghDictionary(languageFolder, language);
        translateResult.listEvents.addAll(toghLanguage.read());

        LanguageResult languageResult = new LanguageResult();
        languageResult.name = language;
        translateResult.listLanguages.add(languageResult);

        // first, check all missing sentences
        if (toghReference.getDictionary() != null)
          for (SentenceItem sentenceEntry : toghReference.getDictionary()) {
            if (toghLanguage.exist(sentenceEntry.key)) {
              // override the original sentence
              toghLanguage.setSentence(sentenceEntry.key, toghLanguage.getTranslation(sentenceEntry.key), sentenceEntry.translation);
              continue;
            }
            languageResult.nbMissingSentences++;
            if (!translate)
              continue;
            if ("en".equals(language)) {
              // no translation needed here
              languageResult.nbTranslatedSentences++;
              toghLanguage.setSentence(sentenceEntry.key, sentenceEntry.translation, sentenceEntry.translation);
            } else {
              if (countTranslation > 1000)
                break;

              countTranslation++;
              // traduction needed
              chronoTranslate.start();
              TranslateSentenceResult translation = translatorGoogle.translateSentence(sentenceEntry.translation, "en", language);
              chronoTranslate.stop();

              translateResult.listEvents.addAll(translation.listEvents);
              if (!LogEventFactory.isError(translation.listEvents)) {
                languageResult.nbTranslatedSentences++;
                toghLanguage.setSentence(sentenceEntry.key, translation.getTranslation(), sentenceEntry.translation);
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

      Collections.sort(translateResult.listLanguages, new Comparator<LanguageResult>() {

        public int compare(LanguageResult s1, LanguageResult s2) {
          return s1.name.compareTo(s2.name);
        }
      });

    } catch (Exception e) {
      StringWriter sw = new StringWriter();
      e.printStackTrace(new PrintWriter(sw));
      String exceptionDetails = sw.toString();

      logger.severe(LOG_HEADER + "During operationDictionary " + e + " at " + exceptionDetails);
      translateResult.listEvents.add(new LogEvent(eventDictionaryOperationError, e, e.getMessage()));
    }

    if (translate && !LogEventFactory.isError(translateResult.listEvents))
      translateResult.listEvents.add(eventDictionaryTranslationSuccess);


    return translateResult;
  }

  /**
   * Detect all languages
   *
   * @param directoryLanguage Directory where all languages are
   * @return list of language detected
   */
  private List<String> detectLanguages(File directoryLanguage) {
    List<String> listLanguages = new ArrayList<>();
    try {
      if (directoryLanguage == null) {
        logger.severe("Detectlanguage: Directory Langage is null");
      } else if (directoryLanguage.listFiles() == null) {
        logger.info("Detectlanguage:No files under [" + directoryLanguage.getAbsolutePath() + "]");
      } else {
        listLanguages = Arrays.stream(directoryLanguage.listFiles())
            .filter(f -> f.isFile() && f.getName().endsWith(".json"))
            .map(c -> c.getName().substring(0, c.getName().length() - ".json".length()))
            .collect(Collectors.toList());
        logger.info("Detectlanguage: in [" + directoryLanguage.getAbsolutePath()
            + "] languages: [" + String.join(", ", listLanguages) + "]");
      }
    } catch (Exception e) {
      logger.severe("Can't detect language in directory [" + directoryLanguage.getAbsolutePath() + "] " + e);
    }
    return listLanguages;
  }

  public static class LanguageResult {
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
    public Map<String, Object> chronometers;
  }
}
