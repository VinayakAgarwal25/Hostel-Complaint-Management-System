package com.hostel.cms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HostelCmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(HostelCmsApplication.class, args);
        System.out.println("\n=======================================================");
        System.out.println(" Hostel Complaint Management System Backend is RUNNING!");
        System.out.println(" Base API URL: http://localhost:8080/api");
        System.out.println(" H2 Console (if in dev mode): http://localhost:8080/h2-console");
        System.out.println("=======================================================\n");
    }
}
