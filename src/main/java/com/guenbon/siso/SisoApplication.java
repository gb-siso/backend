package com.guenbon.siso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling // 스케줄링 활성화
@SpringBootApplication
public class SisoApplication {

    public static void main(String[] args) {

        System.out.println("test jenkins work");
        SpringApplication.run(SisoApplication.class, args);
    }

}

