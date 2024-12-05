package com.youcode.ebanking.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PublicController {

    @GetMapping("/notices")
    public String getNotices() {
        return "Here are the system notices.";
    }

    @GetMapping("/contact")
    public String getContactInfo() {
        return "Contact support at support@ebanking.com or call +123456789.";
    }
}
