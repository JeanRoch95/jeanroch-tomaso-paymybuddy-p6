package com.paymybuddy.mapper;

import com.paymybuddy.dto.UserDTO;
import com.paymybuddy.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(User user);
    User fromDTO(UserDTO userDTO);

}
