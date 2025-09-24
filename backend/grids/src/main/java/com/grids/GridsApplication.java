package com.grids;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class GridsApplication {

    public static void main(String[] args) {
        SpringApplication.run(GridsApplication.class, args);
    }

}
