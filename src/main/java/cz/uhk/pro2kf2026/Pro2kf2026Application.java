package cz.uhk.pro2kf2026;

import cz.uhk.pro2kf2026.model.User;
import cz.uhk.pro2kf2026.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class Pro2kf2026Application {

    public static void main(String[] args) {
        SpringApplication.run(Pro2kf2026Application.class, args);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner seedAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin") == null) {
                User user = new User();
                user.setUsername("admin");
                user.setName("Admin");
                user.setPassword(passwordEncoder.encode("heslo"));
                user.setRole("ADMIN");
                userRepository.save(user);
            }
        };
    }

    @Bean
    CommandLineRunner seedUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("alice") == null) {
                User user = new User();
                user.setUsername("alice");
                user.setName("Alice");
                user.setPassword(passwordEncoder.encode("alicepw"));
                user.setRole("USER");
                userRepository.save(user);
            }

            if (userRepository.findByUsername("bob") == null) {
                User user = new User();
                user.setUsername("bob");
                user.setName("Bob");
                user.setPassword(passwordEncoder.encode("bobpw"));
                user.setRole("USER");
                userRepository.save(user);
            }
        };
    }

}
