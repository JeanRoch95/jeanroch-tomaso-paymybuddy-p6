package com.paymybuddy.controller;

import com.paymybuddy.dto.BankAccountDTO;
import com.paymybuddy.dto.UserDTO;
import com.paymybuddy.dto.UserInformationDTO;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.repository.BankAccountRepository;
import com.paymybuddy.service.BankAccountService;
import com.paymybuddy.service.UserService;
import com.paymybuddy.utils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    private BankAccountService bankAccountService;

    private UserService userService;


    public UserController(BankAccountService bankAccountService, UserService userService) {
        this.bankAccountService = bankAccountService;
        this.userService = userService;
    }

    @RequestMapping("/profil")
    public String profilPage(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BankAccountDTO> bankList = bankAccountService.getSortedBankAccountByCurrentUserId(pageable);

        UserDTO userDTO = userService.getUserByCurrentId();
        UserInformationDTO userInformationDTO = userService.getCurrentUserInformation(userDTO);

        model.addAttribute("hasBanks", !bankList.isEmpty());
        model.addAttribute("user", userInformationDTO);
        model.addAttribute("page", bankList);
        model.addAttribute("banks", bankList.getContent());
        return "/profil";
    }

    @RequestMapping("/profil-update")
    public String profilUpdatePage(Model model) {

        UserDTO userDTO = userService.getUserByCurrentId();
        UserInformationDTO userInformationDTO = userService.getCurrentUserInformation(userDTO);

        model.addAttribute("user", userInformationDTO);
        return "/profil_modify";
    }

    @PostMapping("/profil-update")
    public String updateProfile(@ModelAttribute UserInformationDTO updatedUserInfo, RedirectAttributes redirectAttributes) {
        userService.updateCurrentUserInformation(updatedUserInfo);

        redirectAttributes.addFlashAttribute("successMessage", "Vos informations ont été mises à jour avec succès!");

        return "redirect:/profil";
    }


}
