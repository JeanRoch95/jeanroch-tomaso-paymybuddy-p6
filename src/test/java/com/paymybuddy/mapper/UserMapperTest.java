package com.paymybuddy.mapper;

import com.paymybuddy.dto.UserDTO;
import com.paymybuddy.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)

public class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    public void shouldMapUserToUserDTO() {
        User user = new User();
        user.setFirstName("testUsername");
        user.setEmail("test@email.com");

        UserDTO userDTO = mapper.toDTO(user);

        assertThat(userDTO.getFirstName()).isEqualTo("testUsername");
        assertThat(userDTO.getEmail()).isEqualTo("test@email.com");


    }

    @Test
    public void shouldMapUserDTOToUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("testUsername");
        userDTO.setEmail("test@email.com");

        User user = mapper.fromDTO(userDTO);

        assertThat(user.getFirstName()).isEqualTo("testUsername");
        assertThat(user.getEmail()).isEqualTo("test@email.com");
    }

}
