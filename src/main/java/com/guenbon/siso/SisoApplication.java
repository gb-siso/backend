package com.guenbon.siso;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling // 스케줄링 활성화
@SpringBootApplication
public class SisoApplication {
    public static void main(String[] args) {
        SpringApplication.run(SisoApplication.class, args);
    }

}

