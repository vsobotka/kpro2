package cz.uhk.pro2kf2026.controller;

import cz.uhk.pro2kf2026.model.Dog;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;

@Controller
public class HelloController {

    @GetMapping("/")
    public String index(Model model) {
        ArrayList<Dog> dogs = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            Dog dog = new Dog("Dog " + i, 10 + i);
            dogs.add(dog);
        }
        model.addAttribute("dogs", dogs);
        return "index";
    }

    @GetMapping("/test")
    @ResponseBody
    public String test(){
        return "<h1>TEST</h1>";
    }

}
