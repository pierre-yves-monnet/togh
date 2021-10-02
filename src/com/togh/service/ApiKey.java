/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/* ******************************************************************************** */
/*                                                                                  */
/*  ApiKey                                                                          */
/*                                                                                  */
/* List keys                                                                        */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

public enum ApiKey {
    HTTP_TOGH_SERVER("HttpToghServer"),
    SMTP_USER_PASSWORD("SmtpUserPassword"),
    SMTP_USER_NAME("SmtpUserName"),
    SMTP_PORT("StmpPort"),
    SMTP_FROM("SmtpFrom"),
    SMTP_HOST("SmtpHost"),
    GEOCODE_API_KEY("geocodeAPIKey"),
    GOOGLE_API_KEY("googleAPIKey"),
    TRANSLATE_KEY_API("TranslateKeyAPI");

    public static final List<ApiKey> listKeysEmail = List.of(SMTP_HOST, SMTP_PORT, SMTP_USER_NAME, SMTP_USER_PASSWORD, SMTP_FROM);


    public static final List<ApiKey> listKeysApi = List.of(TRANSLATE_KEY_API, HTTP_TOGH_SERVER);

    public static final List<ApiKey> listKeysServer = Stream.concat(listKeysEmail.stream(), listKeysApi.stream())
            .collect(Collectors.toList());


    public static final List<ApiKey> listKeysBrowser = List.of(GOOGLE_API_KEY, GEOCODE_API_KEY);

    private final String name;

    ApiKey(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * Key using in the browser has a privilege, according the user subscription
     *
     * @return if this key is submitted to the privilege
     */
    public boolean isPrivilegeKey() {
        return (listKeysBrowser.contains(this));
    }

    public static List<ApiKey> getAlls() {
        return Stream.of(ApiKey.values()).collect(Collectors.toList());
    }
}
