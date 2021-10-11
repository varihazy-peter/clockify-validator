package com.vari.clockify.check;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class ClockifyValidatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClockifyValidatorApplication.class, args);
    }

}
