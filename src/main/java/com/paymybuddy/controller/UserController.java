package com.paymybuddy.controller;

import com.paymybuddy.model.BankAccount;
import com.paymybuddy.repository.BankAccountRepository;
import com.paymybuddy.service.BankAccountService;
import com.paymybuddy.utils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    private BankAccountService bankAccountService;

    private BankAccountRepository bankAccountRepository;

    public UserController(BankAccountService bankAccountService, BankAccountRepository bankAccountRepository) {
        this.bankAccountService = bankAccountService;
        this.bankAccountRepository = bankAccountRepository;
    }

    @RequestMapping("/profil")
    public String profilPage(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BankAccount> bankList = bankAccountService.getBankAccountByUserId(SecurityUtils.getCurrentUserId(), pageable);

        model.addAttribute("page", bankList);
        model.addAttribute("banks", bankList.getContent());
        return "profil";
    }

}
