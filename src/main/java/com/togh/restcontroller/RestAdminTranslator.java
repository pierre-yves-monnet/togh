/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.restcontroller;

import com.togh.admin.translate.TranslateDictionary;
import com.togh.admin.translate.TranslateDictionary.TranslateResult;
import com.togh.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/* -------------------------------------------------------------------- */
/*                                                                      */
/* RestAdminTranslator */
/*                                                                      */
/* -------------------------------------------------------------------- */

@RestController
@RequestMapping("togh")
public class RestAdminTranslator {

  @Autowired
  private LoginService loginService;

  @Autowired
  private TranslateDictionary translateDictionnary;

  /**
   * @param connectionStamp Information on the connected user
   * @return Translation status
   */
  @CrossOrigin
  @GetMapping(value = "/api/admin/translator/status", produces = "application/json")
  public TranslateResult translatorStatus(@RequestHeader(RestJsonConstants.PARAM_AUTHORIZATION) String connectionStamp) {

    loginService.isAdministratorConnected(connectionStamp);

    return translateDictionnary.check();

  }

  /**
   * @param connectionStamp Information on the connected user
   * @return Translation result
   */
  @CrossOrigin
  @PostMapping(value = "/api/admin/translator/complete", produces = "application/json")
  public TranslateResult translator(@RequestHeader(RestJsonConstants.PARAM_AUTHORIZATION) String connectionStamp) {

    loginService.isAdministratorConnected(connectionStamp);

    return translateDictionnary.complete();

  }


}
