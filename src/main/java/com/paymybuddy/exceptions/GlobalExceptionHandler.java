package com.paymybuddy.exceptions;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public String handleException(Exception e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        return "error";
    }

    @ExceptionHandler(value = DatabaseException.class)
    public ModelAndView handleDatabaseException(DatabaseException e) {
        ModelAndView mav = new ModelAndView("error/database_error");
        mav.addObject("errorMessage", e.getMessage());
        return mav;
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public String handleInsufficientBalance(InsufficientBalanceException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/transfer";
    }

    @ExceptionHandler(NullTransferException.class)
    public String HandleNullTransfer(NullTransferException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/bank_account_add";
    }

    @ExceptionHandler(IbanAlreadyExistsException.class)
    public String HandleIbanAlreadyExist(IbanAlreadyExistsException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/transfer";
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ModelAndView HandleUserNotFound(UserNotFoundException e) {
        ModelAndView mav = new ModelAndView("error/database_error");
        mav.addObject("errorMessage", e.getMessage());
        return mav;
    }

    @ExceptionHandler(ContactNofFoundException.class)
    public String handleContactNotFound(ContactNofFoundException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/user-connection-add";
    }

    @ExceptionHandler(UserAlreadyAddException.class)
    public String handleContactNotFound(UserAlreadyAddException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/user-connection-add";
    }

    @ExceptionHandler(BankAccountNotFoundException.class)
    public String handleBankAccountNotFoundException(BankAccountNotFoundException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/user-connection-add";
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public String handleUserAlreadyExistException(UserAlreadyExistException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/register";
    }

    @ExceptionHandler(InvalidIdentifiantException.class)
    public String handleInvalidIdentifiantException(InvalidIdentifiantException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/login";
    }

    @ExceptionHandler(InvalidAmountException.class)
    public String handleInvalidAmountException(InvalidAmountException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/bank-money-send";
    }

    @ExceptionHandler(WrongPasswordException.class)
    public String handleWrongPasswordException(WrongPasswordException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/bank-money-send"; // TODO Redirection
    }

}
