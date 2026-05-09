package cz.uhk.pro2kf2026.service;

import cz.uhk.pro2kf2026.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService extends UserDetailsService {
    User getUser(long id);
    void saveUser(User user);
    void deleteUser(long id);
    List<User> getAllUsers();
}
