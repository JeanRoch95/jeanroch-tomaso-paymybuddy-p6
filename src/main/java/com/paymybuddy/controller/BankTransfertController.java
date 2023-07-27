package com.paymybuddy.controller;

import com.paymybuddy.dto.BankTransferDTO;
import com.paymybuddy.exceptions.InsufficientBalanceException;
import com.paymybuddy.exceptions.NullTransferException;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.repository.BankAccountRepository;
import com.paymybuddy.service.BankAccountServiceImpl;
import com.paymybuddy.service.BankTransferServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class BankTransfertController {

    @Autowired
    private BankTransferServiceImpl bankTransferService;

    @Autowired
    private BankAccountServiceImpl bankAccountService;

    @RequestMapping("/bank-money-send")
    public String displayTransferPage(Model model){

        Iterable<BankAccount> bankList = bankAccountService.getBankAccountByCurrentUserId();
        Double balance = bankTransferService.getUserBalance();

        model.addAttribute("bankTransfer", new BankTransferDTO());
        model.addAttribute("banklist", bankList);
        model.addAttribute("balance", balance);
        return "bank_transfer";
    }

    @PostMapping(value = "/bank-money-send")
    public String sendMoney(@Valid @ModelAttribute("bankTransfer") BankTransferDTO bankTransferDTO, @RequestParam("action")String action, RedirectAttributes redirectAttributes, Model model){

        try {
            if("Recevoir".equals(action)) {
                bankTransferService.addMoneyToAccount(bankTransferDTO);
            } else if ("Envoyer".equals(action)) {
                bankTransferService.sendMoneyToBank(bankTransferDTO);
            }
        } catch (InsufficientBalanceException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/bank-money-send";
        } catch (NullTransferException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/bank-money-send";
        }
        redirectAttributes.addFlashAttribute("success", "L'argent a bien été transféré");
        return "redirect:/bank-money-send";
    }


}
