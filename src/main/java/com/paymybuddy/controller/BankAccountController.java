package com.paymybuddy.controller;

import com.paymybuddy.dto.BankAccountDTO;
import com.paymybuddy.dto.BankTransferDTO;
import com.paymybuddy.exceptions.InsufficientBalanceException;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.repository.BankAccountRepository;
import com.paymybuddy.service.BankAccountServiceImpl;
import com.paymybuddy.service.BankTransferServiceImpl;
import com.paymybuddy.utils.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class BankAccountController {

    @Autowired
    private BankAccountServiceImpl bankAccountService;


    @RequestMapping("/bank-account-add")
    public String displayBankAccountAddPage(Model model){
        model.addAttribute("bankAccount", new BankAccount());
        return "bank_account_add";
    }

    @PostMapping(value = "/bank-account-add")
    public String addBankAccount(@Valid @ModelAttribute("bankAccount") BankAccountDTO bankAccountDTO, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "bank_account_add";
        }

        bankAccountService.addBankAccount(bankAccountDTO);
        redirectAttributes.addFlashAttribute("successMessage", "Compte bancaire ajouté avec succès !");
        return "redirect:/profil";
    }

}
