package cz.uhk.pro2kf2026.controller;

import cz.uhk.pro2kf2026.model.Dog;
import cz.uhk.pro2kf2026.service.DogService;
import cz.uhk.pro2kf2026.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/dogs")
public class DogController {

    private final DogService dogService;
    private final UserService userService;

    @Autowired
    public DogController(DogService dogService, UserService userService) {
        this.dogService = dogService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String all(Model model) {
        model.addAttribute("dogs", dogService.getAllDogs());
        return "dogs_list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable("id") long id, Model model) {
        model.addAttribute("dog", dogService.getDog(id));
        return "dogs_detail";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("dog", new Dog());
        model.addAttribute("users", userService.getAllUsers());
        return "dogs_edit";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") long id, Model model) {
        model.addAttribute("dog", dogService.getDog(id));
        model.addAttribute("users", userService.getAllUsers());
        return "dogs_edit";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Dog dog) {
        dogService.saveDog(dog);
        return "redirect:/dogs/";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable("id") long id) {
        dogService.deleteDog(id);
        return "redirect:/dogs/";
    }
}
