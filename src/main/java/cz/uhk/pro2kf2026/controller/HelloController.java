package cz.uhk.pro2kf2026.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/403")
    @ResponseBody
    public String forbidden(Model model) {
        return "<h1>You are not allowed to access this page!</h1>";
    }

    @GetMapping("/admin")
    @ResponseBody
    public String admin(Model model) {
        return "<h1>You are in the Admin section.</h1>";
    }

}
