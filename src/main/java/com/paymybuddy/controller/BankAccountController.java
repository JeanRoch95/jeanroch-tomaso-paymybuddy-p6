package com.paymybuddy.controller;

import com.paymybuddy.dto.BankAccountDTO;
import com.paymybuddy.dto.BankAccountCreateDTO;
import com.paymybuddy.exceptions.IbanAlreadyExistsException;
import com.paymybuddy.service.BankAccountService;
import com.paymybuddy.service.impl.BankAccountServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class BankAccountController {

    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @RequestMapping("/bank-account-add")
    public String displayBankAccountAddPage(Model model){
        model.addAttribute("bankAccount", new BankAccountCreateDTO());
        return "bank_account_add";
    }

    @PostMapping(value = "/bank-account-add")
    public String addBankAccount(
            @Valid @ModelAttribute("bankAccount") BankAccountCreateDTO bankAccountCreateDTO,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "bank_account_add";
        }
        try {
            BankAccountDTO bankAccountDTO = bankAccountService.addBankAccount(bankAccountCreateDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Compte bancaire ajouté avec succès !");
            redirectAttributes.addFlashAttribute("addedAccount", bankAccountDTO);
            return "redirect:/profil";
        } catch (IbanAlreadyExistsException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/bank-account-add";
        }
    }

}
