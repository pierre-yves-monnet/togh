package com.togh.admin.translate;

import java.io.BufferedReader;
import java.io.BufferedWriter;

/* ******************************************************************************** */
/*                                                                                  */
/* ToghDictionary, */
/*                                                                                  */
/* Read/Write one dictionary */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


import org.json.JSONObject;
import org.json.JSONTokener;

import com.togh.engine.logevent.LogEvent;
import com.togh.engine.logevent.LogEvent.Level;

public class ToghDictionary {

    private final static LogEvent eventReadDictionaryError = new LogEvent(ToghDictionary.class.getName(), 1, Level.ERROR, "Read Dictionary error", "Reading an dictionary failed", "Dictionary is empty", "Check Exception");
    private final static LogEvent eventWriteDictionaryError = new LogEvent(ToghDictionary.class.getName(), 2, Level.ERROR, "Write Dictionary error", "Writting an dictionary failed", "Dictionary will be empty", "Check Exception");

    File path;
    String language;
    Map<String, String> dictionary;
    /**
     * marker to know if the dictionnary is modified or not
     */
    private boolean dictionaryIsModified = false;

    public ToghDictionary(File path, String language) {
        this.path = path;
        this.language = language;

    }

    /* -------------------------------------------------------------------- */
    /*                                                                      */
    /* Read/Write */
    /*                                                                      */
    /* -------------------------------------------------------------------- */

    /**
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<LogEvent> read() {
        List<LogEvent> listEvents = new ArrayList<>();
        try {
            File file = getFile();
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
            
            JSONTokener tokener = new JSONTokener(isr);
            JSONObject object = new JSONObject(tokener);
            dictionary = (Map<String, String>) ((Object) object.toMap());
            
            dictionaryIsModified = false;
        } catch (Exception e) {
            listEvents.add(new LogEvent(eventReadDictionaryError, "Dictionary [" + language + "] error " + e.getMessage()));
        }
        return listEvents;
    }

    public List<LogEvent> write() {
        List<LogEvent> listEvents = new ArrayList<>();
        FileOutputStream fos=null;
        try {
            
            // file exist before ? Rename it to .bak
            File file = getFile();
            if (file.exists()) {
                String fileName = file.getAbsolutePath();
                fileName = fileName.replace(".json", ".bak");
                File destFile = new File(fileName);
                file.renameTo(destFile);
            }
            fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            BufferedWriter writer = new BufferedWriter(osw);
            
            // don't use JSONObject.writeJSONString(dictionary, writer) : it write all in one line

            org.json.JSONObject json = new JSONObject( dictionary);
            writer.write(json.toString(2));
            
            writer.flush();

        } catch (Exception e) {
            listEvents.add(new LogEvent(eventWriteDictionaryError, "Dictionary [" + language + "] error " + e.getMessage()));
        }
        finally {
            if (fos!=null)
                try { fos.close(); } catch (IOException e) {
                    listEvents.add(new LogEvent(eventWriteDictionaryError, "Dictionary [" + language + "] error during close:" + e.getMessage()));
                }
        }
        return listEvents;
    }

    /* -------------------------------------------------------------------- */
    /*                                                                      */
    /* Operation */
    /*                                                                      */
    /* -------------------------------------------------------------------- */
    public boolean isModified() {
        return dictionaryIsModified;
    }

    public void setSentence(String key, String translation) {
        if (translation == null)
            return;
        if (dictionary == null)
            dictionary = new HashMap<>();

        String currentValue = dictionary.get(key);
        if (currentValue == null || !currentValue.equals(translation))
            dictionaryIsModified = true;

        dictionary.put(key, translation);
    }

    public boolean exist(String key) {
        return (dictionary != null && dictionary.containsKey(key));
    }

    public Set<Entry<String, String>> getDictionary() {
        if (dictionary == null)
            return new HashMap().entrySet();
        return dictionary.entrySet();
    }

    /* -------------------------------------------------------------------- */
    /*                                                                      */
    /* Get the file used for the dictionary */
    /*                                                                      */
    /* -------------------------------------------------------------------- */

    /**
     * Return the file
     * 
     * @return
     */
    protected File getFile() {
        return new File(path + "/" + language + ".json");
    }
}
