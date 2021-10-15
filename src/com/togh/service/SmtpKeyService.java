/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.service;

/* ******************************************************************************** */
/*                                                                                  */
/*  SmtpKeyService,                                                                 */
/*                                                                                  */
/* methods to get all Smtp Key Value                                                */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

public interface SmtpKeyService {
    String getSmtpHost();

    int getSmtpPort();

    String getSmtpUserName();

    String getSmtpUserPassword();

    String getSmtpFrom();
}
