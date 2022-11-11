package com.togh.restcontroller;


import com.togh.entity.ToghUserEntity;
import com.togh.eventgrantor.update.FactoryUpdateGrantor;
import com.togh.serialization.BaseSerializer;
import com.togh.serialization.FactorySerializer;
import com.togh.serialization.SerializerOptions;
import com.togh.serialization.ToghUserSerializer;
import com.togh.service.FactoryService;
import com.togh.service.LoginService;
import com.togh.service.ToghUserService;
import com.togh.service.ToghUserService.OperationUser;
import com.togh.service.ToghUserService.SearchUsersResult;
import com.togh.service.UnderAttackService;
import com.togh.tool.ToolCast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/* -------------------------------------------------------------------- */
/*                                                                      */
/* RestUserControler */
/*                                                                      */
/* -------------------------------------------------------------------- */

@RestController
@RequestMapping("togh")
public class RestUserController {

  private final Logger logger = Logger.getLogger(RestUserController.class.getName());
  @Autowired
  private FactoryService factoryService;
  @Autowired
  private ToghUserService toghUserService;
  @Autowired
  private LoginService loginService;
  @Autowired
  private FactorySerializer factorySerializer;
  @Autowired
  private FactoryUpdateGrantor factoryUpdateGrantor;
  @Autowired
  private UnderAttackService underAttackService;

  /**
   * Call for the invitation for example, to search a user according some criteria. User should accept to publish some information
   *
   * @param userId          User Id
   * @param connectionStamp Information on the connected user
   * @return all users found plus additional information
   */
  @CrossOrigin
  @GetMapping("/api/user")
  public Map<String, Object> getUser(@RequestParam("id") Long userId,
                                     @RequestParam(name = RestJsonConstants.CST_TIMEZONEOFFSET, required = false) Long timezoneOffset,
                                     @RequestHeader(RestJsonConstants.PARAM_AUTHORIZATION) String connectionStamp) {
    logger.fine("RestUserController:getUser id[" + userId + "]");
    ToghUserEntity toghUser = factoryService.getLoginService().isConnected(connectionStamp);
    if (toghUser == null) {
      underAttackService.reportNotAutorizedAction(null, "/api/user", UnderAttackService.NOT_AUTHORIZED_REASON.NOT_CONNECTED);
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED, RestHttpConstant.CST_HTTPCODE_NOTCONNECTED);
    }
    if (!(userId.equals(toghUser.getId()) || factoryService.getLoginService().isAdministrator(toghUser))) {
      // ask myself, or an administrator, this is OK, else not
      logger.severe("RestUserController:getUser not allowed call[" + userId + "] from toghUser[" + toghUser.getId() + "]");
      underAttackService.reportNotAutorizedAction(toghUser, "/api/user", UnderAttackService.NOT_AUTHORIZED_REASON.CONFIDENTIAL_ACCESS);
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED, RestHttpConstant.CST_HTTPCODE_NOTANADMINISTRATOR);
    }

    ToghUserSerializer toghUserSerializer = (ToghUserSerializer) factorySerializer.getFromEntity(toghUser);

    SerializerOptions serializerOptions = new SerializerOptions(toghUser, timezoneOffset, SerializerOptions.ContextAccess.MYPROFILE);

    return toghUserSerializer.getMap(toghUser, null,
        serializerOptions,
        factorySerializer,
        factoryUpdateGrantor);

  }


  /**
   * Call for the invitation for example, to search a user according some criteria. User should accept to publish some information
   *
   * @param firstName          Search by firstName
   * @param lastName           Search by lastName
   * @param phoneNumber        search by phone
   * @param email              search by Email
   * @param onlyNonInvitedUser Only non invited user in the eventId
   * @param eventId            Event Id for the nonInvitedUser parameter
   * @param connectionStamp    Information on the connected user
   * @return all users found plus additional information
   */
  @CrossOrigin
  @GetMapping("/api/user/search")
  public Map<String, Object> searchUser(@RequestParam(RestJsonConstants.PARAM_FIRST_NAME) String firstName,
                                        @RequestParam(RestJsonConstants.PARAM_LAST_NAME) String lastName,
                                        @RequestParam(RestJsonConstants.PARAM_PHONE_NUMBER) String phoneNumber,
                                        @RequestParam(RestJsonConstants.PARAM_EMAIL) String email,
                                        @RequestParam(RestJsonConstants.PARAM_ONLY_NON_INVITED_USER) Boolean onlyNonInvitedUser,
                                        @RequestParam(RestJsonConstants.INJSON_EVENTID) Long eventId,
                                        @RequestParam(name = RestJsonConstants.CST_TIMEZONEOFFSET, required = false) Long timezoneOffset,
                                        @RequestHeader(RestJsonConstants.PARAM_AUTHORIZATION) String connectionStamp) {
    ToghUserEntity toghUser = factoryService.getLoginService().isConnected(connectionStamp);
    if (toghUser == null)
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED, RestHttpConstant.CST_HTTPCODE_NOTCONNECTED);

    Map<String, Object> payload = new HashMap<>();
    SearchUsersResult searchUsers;
    if (Boolean.TRUE.equals(onlyNonInvitedUser) && eventId != null)
      searchUsers = factoryService.getToghUserService().searchUsersOutEvent(firstName, lastName, phoneNumber, email, eventId, 0, 20);
    else
      searchUsers = factoryService.getToghUserService().searchUsers(firstName, lastName, phoneNumber, email, 0, 20);


    List<Map<String, Object>> listUsersMap = new ArrayList<>();
    for (ToghUserEntity toghUserEntityIterator : searchUsers.listUsers) {
      BaseSerializer serializer = factorySerializer.getFromEntity(toghUserEntityIterator);
      SerializerOptions serializerOptions = new SerializerOptions(toghUser, timezoneOffset, SerializerOptions.ContextAccess.SEARCH);
      listUsersMap.add(serializer.getMap(toghUserEntityIterator, null, serializerOptions, factorySerializer, factoryUpdateGrantor));
    }

    payload.put(RestJsonConstants.LISTUSERS, listUsersMap);
    payload.put(RestJsonConstants.NUMBER_OF_ITEMS, searchUsers.countUsers);
    payload.put(RestJsonConstants.PAGE, searchUsers.page);
    payload.put(RestJsonConstants.ITEMS_PER_PAGE, searchUsers.numberPerPage);
    payload.put(RestJsonConstants.NUMBER_OF_PAGES, (int) (searchUsers.countUsers / searchUsers.numberPerPage) + 1);

    return payload;

  }


  /**
   * Update the profile of user
   *
   * @param updateMap       Information to update user
   * @param connectionStamp Information on the connected user
   * @return result of update
   */
  @CrossOrigin
  @PostMapping(value = "/api/user/update", produces = "application/json")
  public Map<String, Object> updateUser(@RequestBody Map<String, Object> updateMap,
                                        @RequestHeader(RestJsonConstants.PARAM_AUTHORIZATION) String connectionStamp) {
    ToghUserEntity toghUser = factoryService.getLoginService().isConnected(connectionStamp);
    if (toghUser == null)
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED, RestHttpConstant.CST_HTTPCODE_NOTCONNECTED);

    Long timezoneOffset = ToolCast.getLong(updateMap, RestJsonConstants.CST_TIMEZONEOFFSET, 0L);

    String attribut = ToolCast.getString(updateMap, RestJsonConstants.PARAM_ATTRIBUT, "");
    Object value = updateMap.get(RestJsonConstants.PARAM_VALUE);

    Map<String, Object> payload = new HashMap<>();
    OperationUser operationUser = toghUserService.updateUser(toghUser.getId(), attribut, value);
    if (operationUser.toghUserEntity != null) {
      BaseSerializer serializer = factorySerializer.getFromEntity(operationUser.toghUserEntity);
      SerializerOptions serializerOptions = new SerializerOptions(toghUser, timezoneOffset, SerializerOptions.ContextAccess.ADMIN);
      payload.put(RestJsonConstants.USER, serializer.getMap(operationUser.toghUserEntity, null, serializerOptions, factorySerializer, factoryUpdateGrantor));
    }
    payload.put(RestJsonConstants.CST_LOG_EVENTS, operationUser.listLogEvents);

    return payload;
  }

  /**
   * Update the TakeATour option
   *
   * @param active          True if the take a tour policy become active
   * @param connectionStamp Information on the connected user
   * @return result of update
   */
  @CrossOrigin
  @PostMapping(value = "/api/user/takeatour", produces = "application/json")
  public Map<String, Object> takeATour(@RequestParam(RestJsonConstants.PARAM_ACTIVE) Boolean active,
                                       @RequestHeader(RestJsonConstants.PARAM_AUTHORIZATION) String connectionStamp) {

    ToghUserEntity toghUser = factoryService.getLoginService().isConnected(connectionStamp);
    if (toghUser == null)
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED, RestHttpConstant.CST_HTTPCODE_NOTCONNECTED);
    OperationUser operationUser = toghUserService.updateUser(toghUser.getId(), "showTakeATour", active);
    Map<String, Object> payload = new HashMap<>();
    payload.put(RestJsonConstants.CST_LOG_EVENTS, operationUser.listLogEvents);

    return payload;
  }

  /**
   * Update the TakeATour option
   *
   * @param active          True if the take a tour policy become active
   * @param connectionStamp Information on the connected user
   * @return result of update
   */
  @CrossOrigin
  @PostMapping(value = "/api/user/tips", produces = "application/json")
  public Map<String, Object> tips(@RequestParam(RestJsonConstants.PARAM_ACTIVE) Boolean active,
                                  @RequestHeader(RestJsonConstants.PARAM_AUTHORIZATION) String connectionStamp) {

    ToghUserEntity toghUser = factoryService.getLoginService().isConnected(connectionStamp);
    if (toghUser == null)
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED, RestHttpConstant.CST_HTTPCODE_NOTCONNECTED);
    OperationUser operationUser = toghUserService.updateUser(toghUser.getId(), "showTipsUser", active);
    Map<String, Object> payload = new HashMap<>();
    payload.put(RestJsonConstants.CST_LOG_EVENTS, operationUser.listLogEvents);

    return payload;
  }

}
