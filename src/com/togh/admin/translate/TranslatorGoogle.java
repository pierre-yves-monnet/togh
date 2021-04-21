package com.togh.admin.translate;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.togh.engine.logevent.LogEvent;
import com.togh.engine.logevent.LogEvent.Level;
import com.togh.service.ApiKeyService;

@Configuration
@PropertySource("classpath:secret.properties")
public class TranslatorGoogle {

    private Logger logger = Logger.getLogger(TranslatorGoogle.class.getName());
    private final static String logHeader = "com.togh.EventService";

    private final static LogEvent eventGoogleFileDoesNotExist = new LogEvent(TranslatorGoogle.class.getName(), 1, Level.ERROR, "Google File does not exist", "To connect to Google Service, an authentication file is necessary. This file can't be found", "No google service are available", "Check source of the file");
    private final static LogEvent eventGoogleInvalidKey = new LogEvent(TranslatorGoogle.class.getName(), 1, Level.ERROR, "Google InvalidKey", "To connect to Google Service, an authentication key is necessary. This key is incorrect", "No google service are available", "Check source of the file");
    // final String KEY = "AIzaSyB85BFbfSvuyEhrIpibitXldwaSm6Ip5es";

    @Autowired
    private ApiKeyService apiKeyService;
    
    private static Translate translate;

    
    // @Value( "${google.TranslateKeyAPI}" )
    // private String googleApiKey;
    
    
    @SuppressWarnings("deprecation")
    public List<LogEvent> initialisation() {
        List<LogEvent> listEvents = new ArrayList<>();
        // String googleFileName = "D:/dev/git/togh/configuration/Togh-2021-a38140b44a29.json";
        // String googleFileName = "D:/dev/git/togh/configuration/client_secret.json";

        try {
            String googleApikey = apiKeyService.getApiKeyGoogleTranslate(); 
            // authExplicit(googleFileName);
            translate = TranslateOptions.newBuilder().setApiKey( googleApikey ).build().getService();

            
             /* } catch (IOException e) {
             * listEvents.add( new LogEvent(eventGoogleFileDoesNotExist, "File ["+googleFileName+"]"));
             * logger.severe(logHeader+" Initialisation translator "+e.getMessage());
             */
        } catch (Exception e) {
            listEvents.add(new LogEvent(eventGoogleInvalidKey, e, e.getMessage()));
            logger.severe(logHeader + " Initialisation translator " + e.getMessage());
        }
        return listEvents;
    }

    public class TranslateSentenceResult {

        List<LogEvent> listEvents = new ArrayList<>();
        List<String> listTranslations = new ArrayList<>();
        /**
         * In case only one sentence is asked, here the result is saved
         */
        public String getTranslation() {
            if (! listTranslations.isEmpty())
                return listTranslations.get(0);
            return null;
        }
    }

    /**
     * Translate a unique sentence from the Source language to the target langage 
     * @param sentence
     * @param language &#39;
     * @return
     */
    public TranslateSentenceResult translateSentence(String sentence, String sourceLanguage, String targetLanguage) {
        List<String> sentences = new ArrayList<>();
        sentences.add(sentence);
        return translateSentences(sentences, sourceLanguage, targetLanguage);
    }
    
    /**
     * Translate a list of sentence from the source language to the target langage
     * Replace the sequence &#39; by ' : google encode this character, and React does not need that 
     * @param sentences
     * @param sourceLanguage
     * @param targetLanguage
     * @return
     */
    public TranslateSentenceResult translateSentences(List<String> sentences, String sourceLanguage, String targetLanguage) {
        // list languages
        TranslateSentenceResult translateResult = new TranslateSentenceResult();
        // perform
        try {
            for (String sentence : sentences) {

                TranslateOption sourceLanguageOption = Translate.TranslateOption.sourceLanguage(sourceLanguage);
                TranslateOption targetLanguageOption = Translate.TranslateOption.targetLanguage(targetLanguage);

                Translation translation = translate.translate(
                        sentence,
                        sourceLanguageOption,
                        targetLanguageOption);
                // Use "base" for standard edition, "nmt" for the premium model.
                // Translate.TranslateOption.model("base"));
                String decodeSentence = translation.getTranslatedText().replace("&#39;", "'");
                translateResult.listTranslations.add( decodeSentence );
            }

        } catch (Exception e) {
            translateResult.listEvents.add(new LogEvent(eventGoogleInvalidKey, e, e.getMessage()));
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionDetails = sw.toString();

            logger.severe(logHeader + " Initialisation translator " + e.getMessage() + " at " + exceptionDetails);

        }
        return translateResult;
    }

    // https://cloud.google.com/docs/authentication/production#passing_code
    // https://cloud.google.com/docs/authentication/production?hl=fr
    private void authExplicit(String jsonPath) throws IOException {
        // You can specify a credential file by providing a path to GoogleCredentials.
        // Otherwise credentials are read from the GOOGLE_APPLICATION_CREDENTIALS environment variable.
        FileInputStream file = new FileInputStream(jsonPath);

        GoogleCredentials credentials = GoogleCredentials.fromStream(file);
        credentials.createScoped(Arrays.asList("https://www.googleapis.com/auth/cloud-platform"));
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();

        System.out.println("Buckets:");
        Page<Bucket> buckets = storage.list();
        for (Bucket bucket : buckets.iterateAll()) {
            System.out.println(bucket.toString());
        }

    }

}
