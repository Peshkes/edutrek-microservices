package com.telran.lecturerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class LecturerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LecturerServiceApplication.class, args);
    }

}
