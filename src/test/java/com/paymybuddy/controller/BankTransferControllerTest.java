package com.paymybuddy.controller;

import com.paymybuddy.constant.TransactionTypeEnum;
import com.paymybuddy.dto.BankAccountDTO;
import com.paymybuddy.dto.BankTransferCreateDTO;
import com.paymybuddy.dto.BankTransferInformationDTO;
import com.paymybuddy.exceptions.InsufficientBalanceException;
import com.paymybuddy.exceptions.NullTransferException;
import com.paymybuddy.service.AccountService;
import com.paymybuddy.service.BankAccountService;
import com.paymybuddy.service.BankTransferService;
import com.paymybuddy.service.impl.BankAccountServiceImpl;
import com.paymybuddy.service.impl.BankTransferServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BankTransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BankTransferService bankTransferService;

    @MockBean
    private BankAccountService bankAccountService;

    @MockBean
    private AccountService accountService;

    @Test
    public void testDisplayTransferPage() throws Exception {
        int page = 0;
        int size = 10;

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        List<BankTransferInformationDTO> bankTransferInformationDTOList = new ArrayList<>();
        Page<BankTransferInformationDTO> transactionPage = new PageImpl<>(bankTransferInformationDTOList, pageRequest, bankTransferInformationDTOList.size());
        List<BankAccountDTO> bankAccountDTOList = new ArrayList<>();
        BigDecimal balance = BigDecimal.valueOf(100.0);

        when(bankTransferService.getTransferDetails(any(Pageable.class))).thenReturn(transactionPage);
        when(accountService.getBankAccountByCurrentUserId()).thenReturn(bankAccountDTOList);
        when(bankTransferService.getUserBalance()).thenReturn(balance);

        mockMvc.perform(get("/bank-money-send")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("bankTransfer", instanceOf(BankTransferCreateDTO.class)))
                .andExpect(model().attribute("banklist", bankAccountDTOList))
                .andExpect(model().attribute("balance", balance))
                .andExpect(model().attribute("page", transactionPage))
                .andExpect(model().attribute("transactions", bankTransferInformationDTOList))
                .andExpect(view().name("bank_transfer"));

        verify(bankTransferService).getTransferDetails(any(Pageable.class));
        verify(accountService).getBankAccountByCurrentUserId();
        verify(bankTransferService).getUserBalance();
    }

    @Test
    public void testSendMoney_WithInsufficientBalanceException() throws Exception {
        BankTransferCreateDTO bankTransferCreateDTO = new BankTransferCreateDTO();
        bankTransferCreateDTO.setType(TransactionTypeEnum.TransactionType.DEBIT); // Assuming you need to set this

        doThrow(new InsufficientBalanceException("Solde insuffisant pour le transfer."))
                .when(bankTransferService)
                .processBankTransfer(any(BankTransferCreateDTO.class));

        mockMvc.perform(post("/bank-money-send")
                        .flashAttr("bankTransfer", bankTransferCreateDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bank-money-send"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(bankTransferService).processBankTransfer(bankTransferCreateDTO);
    }


    @Test
    public void testSendMoney_WithNullTransferException() throws Exception {
        BankTransferCreateDTO bankTransferCreateDTO = new BankTransferCreateDTO();
        bankTransferCreateDTO.setType(TransactionTypeEnum.TransactionType.DEBIT);

        doThrow(new NullTransferException("Le montant de la transaction ne doit pas être nul"))
                .when(bankTransferService)
                .processBankTransfer(any(BankTransferCreateDTO.class));

        mockMvc.perform(post("/bank-money-send")
                        .flashAttr("bankTransfer", bankTransferCreateDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bank-money-send"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(bankTransferService).processBankTransfer(bankTransferCreateDTO);
    }


    @Test
    public void testSendMoney_debitFromBank() throws Exception {
        BankTransferCreateDTO bankTransferCreateDTO = new BankTransferCreateDTO();
        bankTransferCreateDTO.setType(TransactionTypeEnum.TransactionType.DEBIT); // Assuming you need to set this

        mockMvc.perform(post("/bank-money-send")
                        .flashAttr("bankTransfer", bankTransferCreateDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bank-money-send"))
                .andExpect(flash().attributeExists("success"));

        verify(bankTransferService).processBankTransfer(bankTransferCreateDTO);
    }


    @Test
    public void testSendMoney_creditFromBank() throws Exception {
        BankTransferCreateDTO bankTransferCreateDTO = new BankTransferCreateDTO();
        bankTransferCreateDTO.setType(TransactionTypeEnum.TransactionType.CREDIT); // Assuming you need to set this

        mockMvc.perform(post("/bank-money-send")
                        .flashAttr("bankTransfer", bankTransferCreateDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bank-money-send"))
                .andExpect(flash().attributeExists("success"));

        verify(bankTransferService).processBankTransfer(bankTransferCreateDTO);
    }

}
