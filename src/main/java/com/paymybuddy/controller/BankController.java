package com.paymybuddy.controller;

import com.paymybuddy.model.Bank;
import com.paymybuddy.service.BankService;
import com.paymybuddy.service.BankServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BankController {

    @Autowired
    private BankServiceImpl bankService;

    @RequestMapping("/bank")
    public String getTransferPage(){
        return "bank_transfer";
    }

    @PostMapping(value = "/addbank")
    public String addBank(@RequestParam("bankIban") String iban, @RequestParam("bankSwift") String swift, @RequestParam("bankName") String name) {
        bankService.addBank(iban, swift, name);
        return "redirect:/bank";
    }
}
