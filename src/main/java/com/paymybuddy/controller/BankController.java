package com.paymybuddy.controller;

import com.paymybuddy.dto.BankTransferDTO;
import com.paymybuddy.model.Bank;
import com.paymybuddy.model.BankTransfer;
import com.paymybuddy.repository.BankRepository;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.BankService;
import com.paymybuddy.service.BankServiceImpl;
import com.paymybuddy.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.model.IModel;

import java.util.Iterator;
import java.util.List;

@Controller
public class BankController {

    @Autowired
    private BankServiceImpl bankService;

    @Autowired
    private BankRepository bankRepository;

    @RequestMapping("/bank_add")
    public String getTransferPage(){
        return "bank_add";
    }

    @PostMapping(value = "/addbank")
    public String addBank(@RequestParam("bankIban") String iban, @RequestParam("bankSwift") String swift, @RequestParam("bankName") String name) {
        bankService.addBank(iban, swift, name);
        return "bank_add_confirmation";
    }

    @RequestMapping("/bank_send")
    public String transferPage(Model model){

        Iterable<Bank> bankList = bankService.findAllBank(SecurityUtils.getCurrentUserId());
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
