/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.restcontroller;

/* ******************************************************************************** */
/*                                                                                  */
/*  RestHttpConstant                                                                */
/*                                                                                  */
/*  List all HTTP Constant code returned by the server                              */
/*  Theses code should be translated on the client side                             */
/*                                                                                  */
/* ******************************************************************************** */

public class RestHttpConstant {
  public static final String CST_HTTPCODE_EVENTNOTFOUND = "EventNotFound";
  public static final String CST_HTTPCODE_NOTCONNECTED = "NotConnected";
  public static final String CST_HTTPCODE_NOTANADMINISTRATOR = "NotAdministrator";

  /**
   * only a constant class
   */
  private RestHttpConstant() {
  }

}
