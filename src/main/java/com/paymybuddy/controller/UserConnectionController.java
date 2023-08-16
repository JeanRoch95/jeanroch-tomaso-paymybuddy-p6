package com.paymybuddy.controller;

import com.paymybuddy.dto.UserConnectionInformationDTO;
import com.paymybuddy.exceptions.ContactNofFoundException;
import com.paymybuddy.exceptions.UserAlreadyAddException;
import com.paymybuddy.service.UserConnectionService;
import com.paymybuddy.service.impl.UserConnectionServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserConnectionController {

    private final UserConnectionService userConnectionService;

    public UserConnectionController(UserConnectionService userConnectionService) {
        this.userConnectionService = userConnectionService;
    }

    @RequestMapping("/user-connection-add")
    public String showAddConnectionForm(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<UserConnectionInformationDTO> userConnectionInformationDTOS = userConnectionService.getFriendConnectionList(pageRequest);
        model.addAttribute("userConnection", new UserConnectionInformationDTO());
        model.addAttribute("page", userConnectionInformationDTOS);
        model.addAttribute("hasFriends", !userConnectionInformationDTOS.isEmpty());
        model.addAttribute("connections", userConnectionInformationDTOS.getContent());
        return "contact_add";
    }

    @PostMapping("/user-connection-add")
    public String addConnection(@Valid @ModelAttribute("userConnection") UserConnectionInformationDTO userConnectionInformationDTO,
                                BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "contact_add";
        }


        try {
            userConnectionService.addUserConnection(userConnectionInformationDTO);
        } catch (ContactNofFoundException | UserAlreadyAddException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/user-connection-add";
        }


        redirectAttributes.addFlashAttribute("successMessage", "Connexion ajoutée avec succès!");
        return "redirect:/user-connection-add";
    }

}
