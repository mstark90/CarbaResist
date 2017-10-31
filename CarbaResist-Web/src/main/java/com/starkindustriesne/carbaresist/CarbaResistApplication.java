package com.starkindustriesne.carbaresist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({CarbaResistDBConfig.class, CarbaResistMessagingConfig.class})
@ComponentScan(basePackages={"com.starkindustriesne.carbaresist"})
@EnableAutoConfiguration
public class CarbaResistApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarbaResistApplication.class, args);
    }
}
