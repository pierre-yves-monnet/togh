package com.togh.admin.translate;

import com.togh.engine.logevent.LogEvent;
import com.togh.engine.logevent.LogEvent.Level;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;

public class ToghDictionary {

  private final static LogEvent eventReadDictionaryError = new LogEvent(ToghDictionary.class.getName(), 1, Level.ERROR, "Read Dictionary error", "Reading an dictionary failed", "Dictionary is empty", "Check Exception");
  private final static LogEvent eventWriteDictionaryError = new LogEvent(ToghDictionary.class.getName(), 2, Level.ERROR, "Write Dictionary error", "Writting an dictionary failed", "Dictionary will be empty", "Check Exception");

  File path;
  String language;
  Map<String, SentenceItem> dictionary = new HashMap<String, SentenceItem>();
  /**
   * marker to know if the dictionnary is modified or not
   */
  private boolean dictionaryIsModified = false;

  public ToghDictionary(File path, String language) {
    this.path = path;
    this.language = language;

  }

  /**
   * @return
   */
  @SuppressWarnings("unchecked")
  public List<LogEvent> read() {
    List<LogEvent> listEvents = new ArrayList<>();
    // clear the dictionnary
    dictionary = new HashMap<>();
    File file = getFile();
    try (FileInputStream fis = new FileInputStream(file);
         InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8)) {

      JSONTokener tokener = new JSONTokener(isr);
      JSONObject object = new JSONObject(tokener);
      Map<String, String> brutDictionary = (Map<String, String>) ((Object) object.toMap());
      // rebuild the SentenceItem dictionnary
      for (Entry<String, String> entry : brutDictionary.entrySet()) {
        if (entry.getKey().startsWith("__"))
          continue;
        String originalTranslation = brutDictionary.get("__" + entry.getKey());
        dictionary.put(entry.getKey(), new SentenceItem(entry.getKey(), entry.getValue(), originalTranslation));
      }

      dictionaryIsModified = false;
    } catch (Exception e) {
      listEvents.add(new LogEvent(eventReadDictionaryError, "Dictionary [" + language + "] error " + e.getMessage()));
    }
    return listEvents;
  }

  /* -------------------------------------------------------------------- */
  /*                                                                      */
  /* Read/Write */
  /*                                                                      */
  /* -------------------------------------------------------------------- */

  /**
   * Write the dictionnary
   *
   * @return
   */
  public List<LogEvent> write() {
    List<LogEvent> listEvents = new ArrayList<>();
    FileOutputStream fos = null;
    OutputStreamWriter osw = null;
    BufferedWriter writer = null;
    try {

      // file exist before ? Rename it to .bak
      File file = getFile();
      if (file.exists()) {
        File backupDirectory = new File(file.getParentFile().getAbsolutePath() + "/backup");
        backupDirectory.mkdirs();
        String fileName = file.getName();
        fileName = fileName.replace(".json", ".bak");
        File destFile = new File(backupDirectory.getAbsolutePath() + "/" + fileName);

        file.renameTo(destFile);
      }
      fos = new FileOutputStream(file);
      osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
      writer = new BufferedWriter(osw);

      // don't use JSONObject.writeJSONString(dictionary, writer) : it write all in one line
      // Move the dictionnary to sentence to the brut dictionnary
      List<SentenceItem> listSentences = new ArrayList<>();
      for (Entry<String, SentenceItem> entry : dictionary.entrySet())
        listSentences.add(entry.getValue());

      Collections.sort(listSentences, new Comparator<SentenceItem>() {

        public int compare(SentenceItem s1,
                           SentenceItem s2) {
          return s1.key.compareTo(s2.key);
        }
      });


      // we want to keep the order, so write the ASCII file directly
      writer.write("{\n");
      for (int i = 0; i < listSentences.size(); i++) {
        SentenceItem sentence = listSentences.get(i);
        if (i > 0)
          writer.write(",\n\n");
        if (sentence.originalSentence != null)
          writer.write("  \"__" + sentence.key + "\" : \"" + sentence.originalSentence + "\",\n");
        writer.write("  \"" + sentence.key + "\" : \"" + sentence.translation + "\"");
      }
      writer.write("}\n");


      writer.flush();

    } catch (Exception e) {
      listEvents.add(new LogEvent(eventWriteDictionaryError, "Dictionary [" + language + "] error " + e.getMessage()));
    } finally {
      try {
        if (writer != null) {
          writer.flush();
          writer.close();
        }
        if (fos != null) {
          fos.close();
        }
      } catch (IOException e) {
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

  public void setSentence(String key, String translation, String originalSentence) {
    if (translation == null)
      return;
    if (dictionary == null)
      dictionary = new HashMap<>();

    SentenceItem currentValue = dictionary.get(key);
    if (currentValue == null || !currentValue.translation.equals(translation))
      dictionaryIsModified = true;

    SentenceItem SentenceItem = new SentenceItem(key, translation, originalSentence);
    dictionary.put(key, SentenceItem);
  }

  public String getTranslation(String key) {
    return dictionary.containsKey(key) ? dictionary.get(key).translation : null;
  }

  public boolean exist(String key) {
    return (dictionary != null && dictionary.containsKey(key));
  }

  public Collection<SentenceItem> getDictionary() {
    if (dictionary == null)
      return new HashMap<String, SentenceItem>().values();
    return dictionary.values();
  }

  /**
   * Return the file
   *
   * @return
   */
  protected File getFile() {
    return new File(path + "/" + language + ".json");
  }

  /* -------------------------------------------------------------------- */
  /*                                                                      */
  /* Get the file used for the dictionary */
  /*                                                                      */
  /* -------------------------------------------------------------------- */

  public static class SentenceItem {

    String key;
    String translation;
    String originalSentence;

    public SentenceItem(String key, String translation, String originalSentence) {
      this.key = key;
      this.translation = translation;
      this.originalSentence = originalSentence;
    }
  }
}
