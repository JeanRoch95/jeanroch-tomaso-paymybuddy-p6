package com.paymybuddy.controller;

import com.paymybuddy.repository.BankRepository;
import com.paymybuddy.service.BankService;
import com.paymybuddy.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UserController {

    private BankService bankService;

    public UserController(BankService bankService) {
        this.bankService = bankService;
    }

    @RequestMapping("/profil")
    public String profilPage(Model model) {
        model.addAttribute("banks", bankService.findAllBank(SecurityUtils.getCurrentUserId()));
        return "profil";
    }

}
