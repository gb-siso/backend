package com.guenbon.siso;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling // 스케줄링 활성화
@SpringBootApplication
public class SisoApplication {

    static Logger log = LoggerFactory.getLogger(SisoApplication.class);

    public static void main(String[] args) {

        System.out.println("test jenkins work");

        log.info("test jenkins work");

        SpringApplication.run(SisoApplication.class, args);
    }

}

