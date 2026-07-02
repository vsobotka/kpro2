package cz.uhk.pro2kf2026.controller.rest;

import cz.uhk.pro2kf2026.model.User;
import cz.uhk.pro2kf2026.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/users")
public class UserRestController {

    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public List<User> all(Model model) {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User detail(@PathVariable("id") long id) {
        return userService.getUser(id);
    }

    @PostMapping("/save")
    public String save(@ModelAttribute User user) {
        userService.saveUser(user);
        return "ok";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable("id") long id) {
        userService.deleteUser(id);
        return "ok";
    }
}
