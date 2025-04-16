package sk.tuke.gamestudio.kakuro.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String homePage() {
        return "index";
    }

    @GetMapping("/rules")
    public String aboutPage() {
        return "rules";
    }

    @GetMapping("/settings")
    public String settingsPage() {
        return "settings";
    }
}
