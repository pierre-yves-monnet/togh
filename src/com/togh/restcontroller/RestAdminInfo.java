/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.restcontroller;

import com.togh.service.AdminParameterService;
import com.togh.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/* -------------------------------------------------------------------- */
/*                                                                      */
/* RestAdminTranslator */
/*                                                                      */
/* -------------------------------------------------------------------- */

@RestController
@RequestMapping("togh")
@Configuration
@PropertySource("classpath:version.properties")

public class RestAdminInfo {
    private static final Logger logger = Logger.getLogger(RestAdminInfo.class.getName());
    private static final String LOG_HEADER = RestAdminInfo.class.getSimpleName() + ": ";

    @Autowired
    private LoginService loginService;

    @Autowired
    private AdminParameterService adminParameterService;

    @Autowired
    private RestPingController restPingController;
    @Autowired
    DataSource dataSource;

    @Value("${togh.version}")
    private String toghVersion;

    /**
     * @param connectionStamp Information on the connected user
     * @return all API Keys
     */
    @CrossOrigin
    @GetMapping(value = "/api/admin/info", produces = "application/json")
    public Map<String, Object> getApiKeys(@RequestHeader(RestJsonConstants.PARAM_AUTHORIZATION) String connectionStamp) {

        loginService.isAdministratorConnected(connectionStamp);
        Map<String, Object> informations = new HashMap<>();

        try {
            RestPingController.CollectedIpAddresses collectedIpAddresses = restPingController.getListIpAddress();
            informations.put("listIpAddresses", collectedIpAddresses.allIpAddressesString);
        } catch (Exception e) {
            informations.put("listIpAddressesError", e.toString());
        }


        // get Parameters
        informations.put("listAdminParameters", adminParameterService.getParameters());

        // get all mains information
        List<Map<String, Object>> listInformations = new ArrayList<>();
        informations.put("listInfos", listInformations);
        try (Connection con = dataSource.getConnection()) {
            listInformations.add(addInformation("Database Vendor", con.getMetaData().getDatabaseProductName()));
            listInformations.add(addInformation("Database Version", con.getMetaData().getDatabaseMajorVersion() + "." + con.getMetaData().getDatabaseMinorVersion()));
        } catch (Exception e) {
            logger.severe(LOG_HEADER + "Can't get database connection " + e);
        }
        listInformations.add(addInformation("Java version", System.getProperty("java.version")));
        listInformations.add(addInformation("Back office version", toghVersion));


        return informations;
    }

    /**
     * @param connectionStamp Information on the connected user
     * @return the status for the update
     */
    @CrossOrigin
    @PostMapping(value = "/api/admin/setadminparameter", produces = "application/json")
    public AdminParameterService.AdminParameterServiceStatus updateAdminParameter(@RequestBody Map<String, Object> requestBody,
                                                                                  @RequestHeader(RestJsonConstants.PARAM_AUTHORIZATION) String connectionStamp) {
        return adminParameterService.setParameters((Map<String, Object>) requestBody.get("adminParameters"));

    }


    public Map<String, Object> addInformation(String name, Object value) {
        Map<String, Object> info = new HashMap<>();
        info.put("name", name);
        info.put("value", value);
        return info;
    }
}
    
