package com.paymybuddy.controller;

import com.paymybuddy.dto.*;
import com.paymybuddy.exceptions.InsufficientBalanceException;
import com.paymybuddy.exceptions.NullTransferException;
import com.paymybuddy.service.impl.FriendTransactionServiceImpl;
import com.paymybuddy.service.UserConnectionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class FriendTransactionController {

    @Autowired
    private FriendTransactionServiceImpl friendTransactionService;

    @Autowired
    private UserConnectionService userConnectionService;

    @RequestMapping("/friend-money-send")
    public String displayFriendTransactionPage(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<FriendTransactionDisplayDTO> transactionPage = friendTransactionService.getTransactionsForUser(pageRequest);

        List<UserConnectionInformationDTO> connectionDTOS = userConnectionService.getAllConnectionByCurrentUser();

        Double balance = friendTransactionService.getCurrentUserBalance();
        Double finalPrice = friendTransactionService.calculateMaxPrice(balance);

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
        } catch (InsufficientBalanceException | NullTransferException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/friend-money-send";
        }
        redirectAttributes.addFlashAttribute("success", "L'argent a bien été envoyé à votre ami");
        return "redirect:/friend-money-send";
    }

}
