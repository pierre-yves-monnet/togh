package com.togh;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import com.togh.service.FactoryService;


// see https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto-traditional-deployment
@SpringBootApplication
public class ServletInitializer extends SpringBootServletInitializer {

    @Autowired
    private FactoryService factoryService;
  
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(ServletInitializer.class);
	}
	 public static void main(String[] args) {
	        SpringApplication.run(ServletInitializer.class, args);
	    }
}
