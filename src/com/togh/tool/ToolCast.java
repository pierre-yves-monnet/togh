/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.tool;

import java.util.List;
import java.util.Map;

/* -------------------------------------------------------------------- */
/*                                                                      */
/* ToolCast */
/*                                                                      */
/* -------------------------------------------------------------------- */

public class ToolCast {

    private ToolCast() {
        // static method class
    }

    public static Long getLong(Map<String, Object> content, String key, Long defaultValue) {
        try {
            if (content.get(key) instanceof Long)
                return (Long) content.get(key);
            return (Long.valueOf(content.get(key).toString()));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static Long getLong(Object longValue, Long defaultValue) {
        try {
            if (longValue instanceof Long)
                return (Long) longValue;
            return (Long.valueOf(longValue.toString()));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @SuppressWarnings("rawtypes")
    public static List getList(Map<String,Object> content, String key, List defaultValue) {
        try {
            if (content.get(key) instanceof List)
                return (List) content.get(key);
            return defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static List<Long> getListLong(Map<String,Object> content, String key, List<Long> defaultValue) {
        try {
            if (content.get(key) instanceof List) {
                // if the ID is very large, the object may be send as a STRING. So, this is the moment to translate it
                List<Object> listValues = (List<Object>) content.get(key);

                for (int i = 0; i < listValues.size(); i++) {
                    if ((!(listValues.get(i) instanceof Long)) && listValues.get(i) != null)
                        listValues.set(i, Long.parseLong(listValues.get(i).toString()));
                }
                return (List) listValues;
            }
            return defaultValue;

        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static String getString(Map<String, Object> content, String key, String defaultValue) {
        try {
            if (content.get(key) instanceof String)
                return (String) content.get(key);
            return content.get(key).toString();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static boolean getBoolean(Map<String, Object> content, String key, boolean defaultValue) {
        try {
            if (content.get(key) instanceof Boolean)
                return (Boolean) content.get(key);
            return "on".equals(content.get(key).toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
