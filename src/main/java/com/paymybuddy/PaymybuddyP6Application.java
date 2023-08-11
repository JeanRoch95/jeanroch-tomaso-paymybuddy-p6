package com.paymybuddy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

import java.util.TimeZone;

@SpringBootApplication
public class PaymybuddyP6Application {

    public static void main(String[] args) {
        SpringApplication.run(PaymybuddyP6Application.class, args);
    }

}
