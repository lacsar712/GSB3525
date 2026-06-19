package com.elderly.care;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.elderly.care.mapper")
public class ElderlyCareApplication {
    public static void main(String[] args) {
        SpringApplication.run(ElderlyCareApplication.class, args);
    }
}
