package com.togh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

// see https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto-traditional-deployment
@SpringBootApplication
@ComponentScan(basePackageClasses = ToghApplication.class)

public class ToghApplication extends SpringBootServletInitializer {

  public static void main(String[] args) {
    SpringApplication.run(ToghApplication.class, args);
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(ToghApplication.class);
  }

}
