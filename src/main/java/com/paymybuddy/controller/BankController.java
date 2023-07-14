package com.paymybuddy.controller;

import com.paymybuddy.dto.BankTransferDTO;
import com.paymybuddy.model.Bank;
import com.paymybuddy.service.BankService;
import com.paymybuddy.service.BankServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BankController {

    @Autowired
    private BankServiceImpl bankService;

    @RequestMapping("/bank")
    public String getTransferPage(){
        return "bank_add";
    }

    @PostMapping(value = "/addbank")
    public String addBank(@RequestParam("bankIban") String iban, @RequestParam("bankSwift") String swift, @RequestParam("bankName") String name) {
        bankService.addBank(iban, swift, name);
        return "redirect:/bank";
    }

    @PostMapping(value = "/send_to_account")
    public void addMoneyToAccount(@RequestParam("bankIban") String iban, @RequestParam("description") String description, @RequestParam("id") Integer id, @RequestParam("amount") Double amount){
        BankTransferDTO bankTransferDTO = new BankTransferDTO();
        bankTransferDTO.setAmount(amount);
        bankTransferDTO.setIban(iban);
        bankTransferDTO.setDescription(description);
        bankService.addMoneyToAccount(bankTransferDTO, id);
    }
}
