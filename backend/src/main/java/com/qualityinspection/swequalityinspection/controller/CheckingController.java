package com.qualityinspection.swequalityinspection.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CheckingController {

    @GetMapping("/authenticated")
    public String authenticated() {
        return "Authenticated";
    }
}
