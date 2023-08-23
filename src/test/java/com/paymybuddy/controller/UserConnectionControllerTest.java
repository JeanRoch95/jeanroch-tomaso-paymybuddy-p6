package com.paymybuddy.controller;

import com.paymybuddy.dto.UserConnectionInformationDTO;
import com.paymybuddy.exceptions.ContactNofFoundException;
import com.paymybuddy.exceptions.UserAlreadyAddException;
import com.paymybuddy.service.impl.UserConnectionServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserConnectionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserConnectionServiceImpl userConnectionService;


    @Test
    public void showAddConnectionForm_ShouldDisplayConnections() throws Exception {
        PageRequest pageRequest = PageRequest.of(0, 5);
        Page<UserConnectionInformationDTO> userConnectionInformationDTOS = new PageImpl<>(Collections.emptyList());

        when(userConnectionService.getFriendConnectionList(any(PageRequest.class))).thenReturn(userConnectionInformationDTOS);

        mockMvc.perform(get("/user-connection-add")
                        .with(SecurityMockMvcRequestPostProcessors.user("username").password("password")))
                .andExpect(status().isOk())
                .andExpect(view().name("contact_add"));
    }

    @Test
    public void addConnection_Success_ShouldRedirectWithSuccessMessage() throws Exception {
        UserConnectionInformationDTO userConnectionInformationDTO = new UserConnectionInformationDTO();
        userConnectionInformationDTO.setReceiverUserId(1L);
        userConnectionInformationDTO.setName("JP");
        userConnectionInformationDTO.setEmail("JP@mail.fr");

        mockMvc.perform(post("/user-connection-add")
                        .with(SecurityMockMvcRequestPostProcessors.user("username").password("password"))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", userConnectionInformationDTO.getName())
                        .param("email", userConnectionInformationDTO.getEmail())
                        .param("receiverUserId", userConnectionInformationDTO.getReceiverUserId().toString())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user-connection-add"))
                .andExpect(flash().attribute("successMessage", "Connexion ajoutée avec succès!"));

        verify(userConnectionService, times(1)).addUserConnection(any());
    }

    @Test
    public void addConnection_ContactNofFound_ShouldRedirectWithErrorMessage() throws Exception {
        UserConnectionInformationDTO userConnectionInformationDTO = new UserConnectionInformationDTO();
        userConnectionInformationDTO.setReceiverUserId(1L);
        userConnectionInformationDTO.setName("JP");
        userConnectionInformationDTO.setEmail("JP@mail.fr");

        doThrow(new ContactNofFoundException("Le contact n'a pas été trouvé."))
                .when(userConnectionService)
                .addUserConnection(any(UserConnectionInformationDTO.class));

        mockMvc.perform(post("/user-connection-add")
                        .with(SecurityMockMvcRequestPostProcessors.user("username").password("password"))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", userConnectionInformationDTO.getName())
                        .param("email", userConnectionInformationDTO.getEmail())
                        .param("receiverUserId", userConnectionInformationDTO.getReceiverUserId().toString())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user-connection-add"))
                .andExpect(flash().attribute("errorMessage", "Le contact n'a pas été trouvé."));

        verify(userConnectionService, times(1)).addUserConnection(any());
    }



}