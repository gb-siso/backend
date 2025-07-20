package com.guenbon.siso;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling // 스케줄링 활성화
@SpringBootApplication
public class SisoApplication {
    public static void main(String[] args) {
        SpringApplication.run(SisoApplication.class, args);
    }

}

