/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.restcontroller;
/* -------------------------------------------------------------------- */
/*                                                                      */
/* Ping */
/*                                                                      */
/* -------------------------------------------------------------------- */


import com.togh.service.AdminParameterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("togh")
public class RestPingController {

  private final static String LOG_HEADER = RestLoginController.class.getSimpleName() + ": ";
  private final Logger logger = Logger.getLogger(RestPingController.class.getName());
  @Autowired
  DataSource dataSource;

  @Autowired
  AdminParameterService adminParameterService;

  @Value("${togh.version}")
  private String toghVersion;

  @Value("${togh.debugLocal:true}")
  private Boolean debugLocal;

  /**
   * Call the ping function
   *
   * @param message message to send back, to verify this is not a cache URL - optional
   * @return ping information
   */
  @CrossOrigin
  @GetMapping(value = "/api/ping", produces = "application/json")
  public Map<String, Object> ping(@RequestParam(required = false) String message,
                                  @RequestParam(required = false, name = "serverInfo") Boolean serverInfo) {
    logger.info(LOG_HEADER + "Ping!");
    Map<String, Object> result = new HashMap<>();
    result.put("now", LocalDateTime.now());
    result.put("version", toghVersion);
    if (message != null)
      result.put("message", message);
    // information on the datasource: are we connected?
    try (Connection con = dataSource.getConnection()) {
      con.getMetaData().getDatabaseProductName();
      result.put("database", "Database is up and running");
    } catch (Exception e) {
      result.put("database", "Can't connect");
    }
    if (Boolean.TRUE.equals(serverInfo)) {
      try {
        CollectedIpAddresses collectedIpAddressCollectes = getListIpAddress();

        result.put("serverHttp", collectedIpAddressCollectes.bestIpAddress);
        result.put("allHttp", collectedIpAddressCollectes.allIpAddresses);
      } catch (Exception e) {
        logger.severe("Can't access IP Address");
        result.put("accessIp", e.toString());
      }
    } else {
      result.put("message", "No server Info asked (add serverinfo=true in URL)");
    }
    return result;
  }


  /**
   * Call the ping function
   *
   * @param message message to send back, to verify this is not a cache URL - optional
   * @return ping information
   */
  @CrossOrigin
  @GetMapping(value = "ping", produces = "application/json")
  public Map<String, Object> toghPing(@RequestParam(required = false) String message) {
    return ping(message, false);
  }

  public CollectedIpAddresses getListIpAddress() throws UnknownHostException, SocketException {
    CollectedIpAddresses collectedIpAddress = new CollectedIpAddresses();
    collectedIpAddress.allIpAddresses.add(InetAddress.getLocalHost());
    for (Iterator<NetworkInterface> it = NetworkInterface.getNetworkInterfaces().asIterator();
         it.hasNext(); ) {
      NetworkInterface network = it.next();
      collectedIpAddress.allIpAddresses.addAll(Collections.list(network.getInetAddresses()));
    }

    collectedIpAddress.allIpAddressesString = collectedIpAddress.allIpAddresses.stream()
        .map(InetAddress::getHostAddress)
        .collect(Collectors.toList());

    // get the best IP
    List<Object> collectedAddressIp = collectedIpAddress.allIpAddresses.stream()
        .filter(w -> !w.isAnyLocalAddress())
        .filter(w -> !w.isLoopbackAddress())
        .map(InetAddress::getHostAddress)
        .filter(w -> !w.startsWith("127"))
        .filter(w -> !w.contains(":"))
        .filter(w -> !w.startsWith("192"))
        .filter(w -> !w.startsWith("172"))
        .collect(Collectors.toList());
    List<String> localIp = collectedIpAddress.allIpAddresses.stream()
        .map(InetAddress::getHostAddress)
        .filter(w -> (w.startsWith("192") || w.startsWith("127") || w.startsWith("172")))
        .collect(Collectors.toList());
    /**
     * This is the local environment (debugger), so let's return the local IP
     */
    if (collectedAddressIp.isEmpty())
      collectedAddressIp.addAll(localIp);

    // if there is a saved IP address, get it
    if (Boolean.TRUE.equals(debugLocal)) {
      // Moving React under spring: this should be not necessary after
      logger.info("Accessing production server in Local Mode : don't get that information");
      collectedIpAddress.bestIpAddress=""; // return an empty address==> React will consider localhost
    } else
    {
      Optional<String> ipAddress = adminParameterService.getParameter(AdminParameterService.AdminParameter.IPADDRESS);
      collectedIpAddress.bestIpAddress = ipAddress.isPresent() ? ipAddress.get() : localIp.get(0);
    }
    return collectedIpAddress;
  }

  /**
   * Collect all IP Address visible by the server
   */
  public static class CollectedIpAddresses {
    List<InetAddress> allIpAddresses = new ArrayList<>();
    List<String> allIpAddressesString = new ArrayList<>();
    String bestIpAddress;
  }
}
