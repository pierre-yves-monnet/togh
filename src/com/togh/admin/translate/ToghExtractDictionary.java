package com.togh.admin.translate;
/* ******************************************************************************** */
/*                                                                                  */
/*  ToghExtractDictionary,                                                                 */
/*                                                                                  */
/*  Dictionary Extract fform                        */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

import com.togh.engine.logevent.LogEvent;
import com.togh.engine.logevent.LogEvent.Level;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ToghExtractDictionary extends ToghDictionary {
    
    private final static LogEvent eventReadExtractDictionaryError = new LogEvent(ToghExtractDictionary.class.getName(), 1, Level.ERROR, "Read Extracted Dictionary error", "Reading the extracted dictionary failed", "Dictionary is empty", "Check Exception");

    
    public ToghExtractDictionary(File path, String language) {
        super( path, language);                
    }
    
    /**
     * Read change
     */
    public List<LogEvent> read()  {
        List<LogEvent> listEvents = new ArrayList<>();
        File file = getFile();
        try( FileInputStream fis = new FileInputStream(file);InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8) ) {
   
        // Map<String, Object> dictionaryExtracted = (HashMap<String, Object>) JSONValue.parse( fileReader );
        JSONTokener tokener = new JSONTokener(isr);
        JSONObject object = new JSONObject(tokener);
        Map<String, Object> dictionaryExtracted = object.toMap();

        
        // format is
        // {
        // "BodyTogh.welcome": {
        //    "defaultMessage": "Welcome to Togh D"
        //  },
        for (Entry<String, Object> entry :dictionaryExtracted.entrySet())  {
            @SuppressWarnings("unchecked")
            String defaultMessage = (String) ((Map<String,Object>)entry.getValue()).get("defaultMessage");
            setSentence( entry.getKey(), defaultMessage, defaultMessage);
        }
        }catch(Exception e ) {
            listEvents.add( new LogEvent(eventReadExtractDictionaryError, "Dictionary ["+language+"] error "+e.getMessage() ));
        }
        
        return listEvents;
    }
    
    /**
     * We never write this dictionary
     */
    public List<LogEvent> write()  {
        return new ArrayList<>();
    }
}
