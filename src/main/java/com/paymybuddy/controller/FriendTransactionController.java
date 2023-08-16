package com.paymybuddy.controller;

import com.paymybuddy.binder.BigDecimalBinderCustomizer;
import com.paymybuddy.dto.*;
import com.paymybuddy.exceptions.InsufficientBalanceException;
import com.paymybuddy.exceptions.InvalidAmountException;
import com.paymybuddy.exceptions.NullTransferException;
import com.paymybuddy.service.AccountService;
import com.paymybuddy.service.BalanceService;
import com.paymybuddy.service.FriendTransactionService;
import com.paymybuddy.service.impl.FriendTransactionServiceImpl;
import com.paymybuddy.service.UserConnectionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.util.List;

@Controller
public class FriendTransactionController {

    private final FriendTransactionService friendTransactionService;

    private final BalanceService balanceService;

    private final AccountService accountService;

    private final BigDecimalBinderCustomizer bigDecimalBinderCustomizer;

    public FriendTransactionController(FriendTransactionService friendTransactionService, BalanceService balanceService, AccountService accountService, BigDecimalBinderCustomizer bigDecimalBinderCustomizer) {
        this.friendTransactionService = friendTransactionService;
        this.balanceService = balanceService;
        this.accountService = accountService;
        this.bigDecimalBinderCustomizer = bigDecimalBinderCustomizer;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        bigDecimalBinderCustomizer.initBinder(binder);
    }

    @RequestMapping("/friend-money-send")
    public String displayFriendTransactionPage(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<FriendTransactionDisplayDTO> transactionPage = friendTransactionService.getTransactionsForUser(pageRequest);

        List<UserConnectionInformationDTO> connectionDTOS = accountService.getAllConnectionByCurrentAccount();

        BigDecimal balance = accountService.getCurrentUserBalance();
        BigDecimal finalPrice = balanceService.calculateMaxPrice(balance);

        model.addAttribute("friendTransaction", new FriendTransactionCreateDTO());
        model.addAttribute("connections", connectionDTOS);
        model.addAttribute("balance", balance);
        model.addAttribute("finalPrice", finalPrice);
        model.addAttribute("hasTransasction", !transactionPage.isEmpty());
        model.addAttribute("page", transactionPage);
        model.addAttribute("transactions", transactionPage.getContent());

        return "friend_transaction";
    }


    @PostMapping(value = "/friend-money-send")
    public String sendMoneyToFriend(@Valid @ModelAttribute("friendTransaction") FriendTransactionCreateDTO friendTransactionCreateDTO, RedirectAttributes redirectAttributes) {

        try {
            friendTransactionService.sendMoneyToFriend(friendTransactionCreateDTO);
        } catch (InsufficientBalanceException | NullTransferException | InvalidAmountException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/friend-money-send";
        }
        redirectAttributes.addFlashAttribute("success", "L'argent a bien été envoyé à votre ami");
        return "redirect:/friend-money-send";
    }

}
