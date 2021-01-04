package com.together.spring;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.together.data.entity.EventEntity;
import com.together.data.entity.base.BaseEntity;
import com.together.repository.spring.EventSpringRepository;
import com.together.service.EventService;
import com.together.service.LoginService;
import com.together.service.LoginService.LoginStatus;
import com.together.service.accessor.MemoryServiceAccessor;
import com.together.service.accessor.ServiceAccessor;
import com.together.service.accessor.SpringServiceAccessor;

/* ******************************************************************************** */
/*                                                                                  */
/*
 * SprintApplication
 * Main front end for all REST call
 */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@SpringBootApplication

// Prefix : @RequestMapping ("/hello")
// @ E n a b  l e J p a  R e p  ositories(basePackages="com.together.repository.spring")
// @ComponentScan("com.together.repository.spring")
public class Application extends SpringBootServletInitializer {

    ServiceAccessor serciceAccessor = new MemoryServiceAccessor();
    // ServiceAccessor serciceAccessor = new SpringServiceAccessor();
    
    
 
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

   
}
