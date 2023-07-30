package com.paymybuddy.controller;

import com.paymybuddy.dto.BankTransferDTO;
import com.paymybuddy.dto.TransactionDTO;
import com.paymybuddy.exceptions.InsufficientBalanceException;
import com.paymybuddy.exceptions.NullTransferException;
import com.paymybuddy.mapper.TransactionMapper;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.repository.BankTransferRepository;
import com.paymybuddy.service.BankAccountServiceImpl;
import com.paymybuddy.service.BankTransferServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    @Autowired
    private BankTransferRepository bankTransferRepository;

    @Autowired
    private TransactionMapper transactionMapper;


    @RequestMapping("/bank-money-send")
    public String displayTransferPage(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<TransactionDTO> transactionPage = bankTransferService.getTransferDetails(pageRequest);

        Iterable<BankAccount> bankList = bankAccountService.getBankAccountByCurrentUserId();
        Double balance = bankTransferService.getUserBalance();

        model.addAttribute("bankTransfer", new BankTransferDTO());
        model.addAttribute("banklist", bankList);
        model.addAttribute("balance", balance);
        model.addAttribute("page", transactionPage);
        model.addAttribute("transactions", transactionPage.getContent());

        return "bank_transfer";
    }



    @PostMapping(value = "/bank-money-send")
    public String sendMoney(@Valid @ModelAttribute("bankTransfer") BankTransferDTO bankTransferDTO, @RequestParam("action")String action, RedirectAttributes redirectAttributes, Model model){

        try {
            if("Recevoir".equals(action)) {
                bankTransferService.creditFromBankAccount(bankTransferDTO);
            } else if ("Envoyer".equals(action)) {
                bankTransferService.debitFromBankAccount(bankTransferDTO);
            }
        } catch (InsufficientBalanceException | NullTransferException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/bank-money-send";
        }
        redirectAttributes.addFlashAttribute("success", "L'argent a bien été transféré");
        return "redirect:/bank-money-send";
    }


}
