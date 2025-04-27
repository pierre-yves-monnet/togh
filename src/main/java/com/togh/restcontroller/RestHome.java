package com.togh.restcontroller;

import com.togh.service.LoginService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.logging.Logger;

@Controller
public class RestHome {
    private final Logger logger = Logger.getLogger(RestHome.class.getName());

    @GetMapping("/")
    public String home() {
        logger.info("RestHome: one access, return [index.html]");
        return "index.html";
    }
}
