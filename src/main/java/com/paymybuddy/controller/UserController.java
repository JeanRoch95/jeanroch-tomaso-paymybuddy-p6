package com.paymybuddy.controller;

import com.paymybuddy.service.BankAccountService;
import com.paymybuddy.utils.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UserController {

    private BankAccountService bankAccountService;

    public UserController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @RequestMapping("/profil")
    public String profilPage(Model model) {
        model.addAttribute("banks", bankAccountService.findAllBank(SecurityUtils.getCurrentUserId()));
        return "profil";
    }

}
