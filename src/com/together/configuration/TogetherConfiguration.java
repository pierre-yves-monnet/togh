package com.together.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import javax.sql.DataSource;

// @Configuration
public class TogetherConfiguration {
    /*
    @Bean
    public DataSource datasource() {
        return DataSourceBuilder.create()
          .driverClassName("org.postgresql.Driver")
          .url("jdbc:postgresql://localhost:5432/together")
          .username("postgres")
          .password("postgres")
          .build(); 
    }
    */
}