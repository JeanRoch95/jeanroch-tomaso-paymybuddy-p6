package com.paymybuddy.controller;

import com.paymybuddy.dto.BankTransferDTO;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.repository.BankAccountRepository;
import com.paymybuddy.service.BankAccountServiceImpl;
import com.paymybuddy.utils.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class BankController {

    @Autowired
    private BankAccountServiceImpl bankService;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @RequestMapping("/bank_add")
    public String getTransferPage(Model model){
        model.addAttribute("bankAccount", new BankAccount());
        return "bank_account_add";
    }

    @PostMapping(value = "/addbank")
    public String addBank(@Valid @ModelAttribute("bankAccount") BankAccount bankAccount, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "bank_account_add";
        }

        bankService.addBank(bankAccount.getIban(), bankAccount.getSwift(), bankAccount.getName());
        return "bank_add_confirmation";
    }

    @RequestMapping("/bank_send")
    public String transferPage(Model model){

        Iterable<BankAccount> bankList = bankService.findAllBank(SecurityUtils.getCurrentUserId());
        model.addAttribute("banklist", bankList);
        return "bank_transfer";
    }

    @PostMapping(value = "/bank_send_account")
    public String addMoneyToAccount(@RequestParam("bankIban") String iban, @RequestParam("description") String description, @RequestParam("amount") Double amount, @RequestParam("action")String action){
        BankTransferDTO bankTransferDTO = new BankTransferDTO(iban, description, amount);
        int id = 1;
        if("Recevoir".equals(action)) {
            bankService.addMoneyToAccount(bankTransferDTO, id);
        } else if ("Envoyer".equals(action)) {
            bankService.sendMoneyToBank(bankTransferDTO, id);
        }

        return "redirect:/bank_send";
    }

}
