package com.paymybuddy.controller;

import com.paymybuddy.dto.BankAccountDTO;
import com.paymybuddy.dto.UserDTO;
import com.paymybuddy.dto.UserInformationDTO;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.service.BankAccountServiceImpl;
import com.paymybuddy.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private BankAccountServiceImpl bankAccountService;


    @Test
    public void testDisplayProfilPageWithBankAccountList() throws Exception {
        List<BankAccountDTO> bankAccountDTOList = new ArrayList<>();
        BankAccountDTO bankAccountDTO = new BankAccountDTO();

        bankAccountDTOList.add(bankAccountDTO);
        Page<BankAccountDTO> page = new PageImpl<>(bankAccountDTOList);

        UserDTO userDTO = new UserDTO();
        UserInformationDTO userInformationDTO = new UserInformationDTO();

        when(bankAccountService.getSortedBankAccountByCurrentUserId(any(Pageable.class))).thenReturn(page);
        when(userService.getUserByCurrentId()).thenReturn(userDTO);
        when(userService.getCurrentUserInformation(userDTO)).thenReturn(userInformationDTO);

        mockMvc.perform(get("/profil"))
                .andExpect(status().isOk())
                .andExpect(view().name("/profil"))
                .andExpect(model().attributeExists("page"))
                .andExpect(model().attributeExists("banks"))
                .andExpect(model().attributeExists("user")); // assurez-vous que "user" existe également
    }


    @Test
    public void testProfilUpdatePage() throws Exception {
        UserDTO userDTO = new UserDTO();
        UserInformationDTO userInformationDTO = new UserInformationDTO();

        when(userService.getUserByCurrentId()).thenReturn(userDTO);
        when(userService.getCurrentUserInformation(userDTO)).thenReturn(userInformationDTO);

        mockMvc.perform(get("/profil-update"))
                .andExpect(status().isOk())
                .andExpect(view().name("/profil_modify"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    public void testUpdateProfile() throws Exception {
        UserInformationDTO userInformationDTO = new UserInformationDTO();

        mockMvc.perform(post("/profil-update")
                        .flashAttr("user", userInformationDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profil"));
    }

}
