package com.paymybuddy.controller;

import com.paymybuddy.dto.LoginFormDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("loginForm", new LoginFormDTO());
        return "login"; // nom de la vue (nom du fichier HTML sans l'extension .html)
    }

    @PostMapping("/login")
    public String handleLogin(@ModelAttribute LoginFormDTO loginForm, Model model, RedirectAttributes redirectAttributes) {


        return "login"; // ou redirigez vers une autre page en cas de succès
    }
}
