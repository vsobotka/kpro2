package cz.uhk.pro2kf2026.controller.rest;

import cz.uhk.pro2kf2026.model.User;
import cz.uhk.pro2kf2026.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class AuthRestController {

    private final UserRepository userRepository;

    public AuthRestController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/api/me")
    public User me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        User user = userRepository.findByUsername(authentication.getName());

        return user;
    }
}
