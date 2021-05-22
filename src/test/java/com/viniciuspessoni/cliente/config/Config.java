package com.viniciuspessoni.cliente.config;

import com.viniciuspessoni.cliente.dto.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

@Configuration
@ComponentScan("preference.data")
public class Config {
    @Bean
    @Scope(SCOPE_CUCUMBER_GLUE)
    public Client client() {
        return new Client();
    }
}
