package com.paymybuddy.controller;

import com.paymybuddy.binder.BigDecimalBinderCustomizer;
import com.paymybuddy.constant.TransactionTypeEnum;
import com.paymybuddy.dto.BankAccountDTO;
import com.paymybuddy.dto.BankTransferCreateDTO;
import com.paymybuddy.dto.BankTransferInformationDTO;
import com.paymybuddy.exceptions.BankAccountNotFoundException;
import com.paymybuddy.exceptions.InsufficientBalanceException;
import com.paymybuddy.exceptions.InvalidAmountException;
import com.paymybuddy.exceptions.NullTransferException;
import com.paymybuddy.service.AccountService;
import com.paymybuddy.service.BalanceService;
import com.paymybuddy.service.BankAccountService;
import com.paymybuddy.service.BankTransferService;
import com.paymybuddy.service.impl.BankAccountServiceImpl;
import com.paymybuddy.service.impl.BankTransferServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;

@Controller
public class BankTransfertController {

    private final BankTransferService bankTransferService;

    private final AccountService accountService;

    private final BigDecimalBinderCustomizer bigDecimalBinderCustomizer;

    private final BalanceService balanceService;

    public BankTransfertController(BankTransferService bankTransferService, AccountService accountService, BigDecimalBinderCustomizer bigDecimalBinderCustomizer, BalanceService balanceService) {
        this.bankTransferService = bankTransferService;
        this.accountService = accountService;
        this.bigDecimalBinderCustomizer = bigDecimalBinderCustomizer;
        this.balanceService = balanceService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        bigDecimalBinderCustomizer.initBinder(binder);
    }

    private void populateModelWithAttributes(Model model, Pageable pageable) {
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createdAt").descending());
        Page<BankTransferInformationDTO> transactionPage = bankTransferService.getTransferDetails(pageRequest);

        Iterable<BankAccountDTO> bankList = accountService.getBankAccountByCurrentUserId();
        BigDecimal balance = balanceService.getCurrentUserBalance();

        model.addAttribute("banklist", bankList);
        model.addAttribute("balance", balance);
        model.addAttribute("hasTransfers", !transactionPage.isEmpty());
        model.addAttribute("page", transactionPage);
        model.addAttribute("transactions", transactionPage.getContent());
    }

    @RequestMapping("/bank-money-send")
    public String displayTransferPage(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        populateModelWithAttributes(model, PageRequest.of(page, size));
        model.addAttribute("bankTransfer", new BankTransferCreateDTO());
        return "bank_transfer";
    }



    @PostMapping(value = "/bank-money-send")
    public String sendMoney(@Valid @ModelAttribute("bankTransfer") BankTransferCreateDTO bankTransferCreateDTO,
                            BindingResult bindingResult,
                            Model model, RedirectAttributes redirectAttributes,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size
    ) {
        if (bindingResult.hasErrors()) {
            populateModelWithAttributes(model, PageRequest.of(page, size));
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "bank_transfer";
        }
        try {
            bankTransferService.processBankTransfer(bankTransferCreateDTO);
        } catch (InsufficientBalanceException | NullTransferException | BankAccountNotFoundException | IllegalArgumentException |
                 InvalidAmountException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/bank-money-send";
        }

        redirectAttributes.addFlashAttribute("success", "L'argent a bien été transféré");
        return "redirect:/bank-money-send";
    }

}
