package com.paymybuddy.controller;

import com.paymybuddy.dto.UserConnectionInformationDTO;
import com.paymybuddy.exceptions.UserAlreadyAddException;
import com.paymybuddy.service.UserConnectionServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;
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
    public void testShowAddConnectionForm() throws Exception {
        UserConnectionInformationDTO dto = new UserConnectionInformationDTO();
        List<UserConnectionInformationDTO> content = Collections.singletonList(dto);
        Page<UserConnectionInformationDTO> page = new PageImpl<>(content);

        when(userConnectionService.getFriendConnectionList(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/user-connection-add"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("userConnection", instanceOf(UserConnectionInformationDTO.class)))
                .andExpect(model().attribute("page", page))
                .andExpect(model().attribute("connections", content))
                .andExpect(view().name("contact_add"));

        verify(userConnectionService, times(1)).getFriendConnectionList(any(PageRequest.class));
    }

    @Test
    public void testAddConnection_Success() throws Exception {
        UserConnectionInformationDTO dto = new UserConnectionInformationDTO();

        mockMvc.perform(post("/user-connection-add")
                        .flashAttr("userConnection", dto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user-connection-add"))
                .andExpect(flash().attribute("successMessage", "Connexion ajoutée avec succès!"));

        verify(userConnectionService, times(1)).addUserConnection(any(UserConnectionInformationDTO.class));
    }

    @Test
    public void testAddConnection_Error() throws Exception {
        UserConnectionInformationDTO dto = new UserConnectionInformationDTO();
        String errorMessage = "Error Message";

        doThrow(new UserAlreadyAddException(errorMessage)).when(userConnectionService).addUserConnection(any(UserConnectionInformationDTO.class));

        mockMvc.perform(post("/user-connection-add")
                        .flashAttr("userConnection", dto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user-connection-add"))
                .andExpect(flash().attribute("errorMessage", errorMessage));

        verify(userConnectionService, times(1)).addUserConnection(any(UserConnectionInformationDTO.class));
    }

}