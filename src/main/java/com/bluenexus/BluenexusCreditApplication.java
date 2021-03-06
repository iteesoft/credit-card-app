package com.bluenexus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableScheduling
@SpringBootApplication
public class BluenexusCreditApplication {
    public static void main(String[] args) {
        SpringApplication.run(BluenexusCreditApplication.class, args);
    }
}
