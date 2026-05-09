package cz.uhk.pro2kf2026;

import cz.uhk.pro2kf2026.model.User;
import cz.uhk.pro2kf2026.service.DogService;
import cz.uhk.pro2kf2026.service.UserService;
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

    /*@Bean
    CommandLineRunner commandLineRunner(UserService userService) {
        return args -> {
            User user = new User();
            user.setUsername("admin");
            user.setName("Admin");
            user.setPassword("heslo");
            user.setRole("ADMIN");
            userService.saveUser(user);
        };
    }*/

}
