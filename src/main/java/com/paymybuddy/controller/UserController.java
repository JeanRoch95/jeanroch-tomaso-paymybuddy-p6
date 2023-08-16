package com.paymybuddy.controller;

import com.paymybuddy.dto.BankAccountDTO;
import com.paymybuddy.dto.UserCreateDTO;
import com.paymybuddy.dto.UserInformationDTO;
import com.paymybuddy.exceptions.UserAlreadyExistException;
import com.paymybuddy.service.AccountService;
import com.paymybuddy.service.BankAccountService;
import com.paymybuddy.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    private final BankAccountService bankAccountService;

    private final UserService userService;

    private final AccountService accountService;


    public UserController(BankAccountService bankAccountService, UserService userService, AccountService accountService) {
        this.bankAccountService = bankAccountService;
        this.userService = userService;
        this.accountService = accountService;
    }

    @RequestMapping("/profil")
    public String profilPage(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BankAccountDTO> bankList = bankAccountService.getSortedBankAccountByCurrentUserId(pageable);

        UserInformationDTO userInformationDTO = accountService.getCurrentAccountInformations();

        model.addAttribute("hasBanks", !bankList.isEmpty());
        model.addAttribute("user", userInformationDTO);
        model.addAttribute("page", bankList);
        model.addAttribute("banks", bankList.getContent());
        return "/profil";
    }

    @RequestMapping("/profil-update")
    public String profilUpdatePage(Model model) {

        UserInformationDTO userInformationDTO = accountService.getCurrentAccountInformations();

        model.addAttribute("user", userInformationDTO);
        return "/profil_modify";
    }

    @PostMapping("/profil-update")
    public String updateProfile(@ModelAttribute UserInformationDTO updatedUserInfo,
                                RedirectAttributes redirectAttributes,
                                HttpServletRequest request,
                                HttpServletResponse response) {

        if(accountService.checkIfEmailChanged(updatedUserInfo)) {
            accountService.updateCurrentUserInformation(updatedUserInfo);
            userService.logoutUser(request, response);
            redirectAttributes.addFlashAttribute("infoMessage", "Votre e-mail a été modifié. Veuillez vous reconnecter.");
            return "redirect:/login";
        } else {
            accountService.updateCurrentUserInformation(updatedUserInfo);
            redirectAttributes.addFlashAttribute("successMessage", "Vos informations ont été mises à jour avec succès!");
            return "redirect:/profil";
        }
    }

    @RequestMapping("/register")
    public String showRegistrationForm(Model model) {

        model.addAttribute("user", new UserCreateDTO());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute("user") UserCreateDTO userCreateDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            userService.createUser(userCreateDTO);
            redirectAttributes.addFlashAttribute("success", "Votre compte a bien été crée.");
            return "redirect:/login";
        } catch (UserAlreadyExistException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register";
        }
    }




}
