package com.togh.controller;

import java.util.Map;

/* -------------------------------------------------------------------- */
/*                                                                      */
/* RestEventControler */
/*                                                                      */
/* -------------------------------------------------------------------- */

public class RestTool {

    public static Long getLong(Map<String,Object> content, String key, Long defaultValue) {
        try
        { 
            if (content.get(key) instanceof Long)
                return (Long) content.get(key);
            return (Long.valueOf( content.get(key).toString()));
        }
        catch(Exception e) {
            return defaultValue;
        }
    }
    
    public static String getString(Map<String,Object> content, String key, String defaultValue) {
        try
        { 
            if (content.get(key) instanceof String)
                return (String) content.get(key);
            return content.get(key).toString();
        }
        catch(Exception e) {
            return defaultValue;
        }
    }
}
