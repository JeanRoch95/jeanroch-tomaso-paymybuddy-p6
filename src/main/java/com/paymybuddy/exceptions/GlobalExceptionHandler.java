package com.paymybuddy.exceptions;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
@ControllerAdvice
public class GlobalExceptionHandler {

    // Gestionnaire pour toutes les autres exceptions
    @ExceptionHandler(value = Exception.class)
    public String handleException(Exception e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        return "error";
    }

    @ExceptionHandler(value = DatabaseException.class)
    public String handleDatabaseException(DatabaseException e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());

        return "error/database_error";
    }

}
