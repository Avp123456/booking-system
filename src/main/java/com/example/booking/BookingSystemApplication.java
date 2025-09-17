package com.example.booking;

import com.example.booking.model.Role;
import com.example.booking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Set;

@SpringBootApplication
@RequiredArgsConstructor
public class BookingSystemApplication {

    private final UserService userService;

    public static void main(String[] args) {
        SpringApplication.run(BookingSystemApplication.class, args);
    }

    @Bean
    public CommandLineRunner seedUsers() {
        return args -> {
            userService.createIfNotExists("admin", "admin123", Set.of(Role.ADMIN));
            userService.createIfNotExists("user", "user123", Set.of(Role.USER));
        };
    }
}
