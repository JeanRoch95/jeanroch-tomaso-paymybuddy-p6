package com.paymybuddy.mapper;

import com.paymybuddy.dto.UserConnectionInformationDTO;
import com.paymybuddy.model.UserConnection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserConnectionMapper {

    @Mapping(source="receiver.firstName", target = "name")
    @Mapping(source="receiver.email", target = "email")
    UserConnectionInformationDTO getFriendConnectionList(UserConnection userConnection);
}
