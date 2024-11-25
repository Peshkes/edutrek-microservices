package com.telran.lecturerservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;

@EnableFeignClients
@SpringBootApplication
public class LecturerServiceApplication implements CommandLineRunner {
    private final ApplicationContext context;

    public LecturerServiceApplication(ApplicationContext context) {
        this.context = context;
    }


    public static void main(String[] args) {
        SpringApplication.run(LecturerServiceApplication.class, args);
    }

    @Override
    public void run(String... args) {
        boolean exists = context.containsBean("lecturerRepository");
        System.out.println("LecturerRepository bean exists: " + exists);
    }


}
