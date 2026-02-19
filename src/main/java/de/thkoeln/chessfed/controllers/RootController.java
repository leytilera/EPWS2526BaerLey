package de.thkoeln.chessfed.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {
    
    @GetMapping("/")
    public String index() {
        return "home";
    }

    @GetMapping("/play")
    public String play() {
        return "play";
    }

    @GetMapping("/replay")
    public String replay() {
        return "replay";
    }
}
