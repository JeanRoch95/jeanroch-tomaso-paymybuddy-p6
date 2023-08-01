package com.paymybuddy.controller;

import com.paymybuddy.dto.BankAccountDTO;
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


    public UserController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @RequestMapping("/profil")
    public String profilPage(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BankAccountDTO> bankList = bankAccountService.getSortedBankAccountByCurrentUserId(pageable);

        model.addAttribute("page", bankList);
        model.addAttribute("banks", bankList.getContent());
        return "/profil";
    }

}
