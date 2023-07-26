package com.paymybuddy.controller;

import com.paymybuddy.dto.BankAccountDTO;
import com.paymybuddy.dto.BankTransferDTO;
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

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private BankTransferServiceImpl bankTransferService;

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

    @RequestMapping("/bank-money-send")
    public String displayTransferPage(Model model){

        Iterable<BankAccount> bankList = bankAccountService.getBankAccountByCurrentUserId();
        Double balance = bankTransferService.getUserBalance();
        model.addAttribute("banklist", bankList);
        model.addAttribute("balance", balance);
        return "bank_transfer";
    }

    @PostMapping(value = "/bank-money-send")
    public String sendMoney(@RequestParam("bankIban") String iban, @RequestParam("description") String description, @RequestParam("amount") Double amount, @RequestParam("action")String action){
        BankTransferDTO bankTransferDTO = new BankTransferDTO(iban, description, amount);
        int id = 1;
        if("Recevoir".equals(action)) {
            bankAccountService.addMoneyToAccount(bankTransferDTO, id);
        } else if ("Envoyer".equals(action)) {
            bankAccountService.sendMoneyToBank(bankTransferDTO, id);
        }

        return "redirect:/bank-money-send";
    }

}
