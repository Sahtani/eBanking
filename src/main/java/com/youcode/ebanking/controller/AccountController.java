package com.youcode.ebanking.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AccountController {

    @GetMapping("/myLoans")
    @PreAuthorize("hasRole('USER')")
    public String getMyLoans() {
        return "Here are your loan details.";
    }

    @GetMapping("/myCards")
    @PreAuthorize("hasRole('USER')")
    public String getMyCards() {
        return "Here are your credit card details.";
    }

    @GetMapping("/myAccount")
    @PreAuthorize("hasRole('USER')")
    public String getMyAccountDetails() {
        return "Here are your account details.";
    }

    @GetMapping("/myBalance")
    @PreAuthorize("hasRole('USER')")
    public String getMyBalance() {
        return "Here is your account balance.";
    }
}
