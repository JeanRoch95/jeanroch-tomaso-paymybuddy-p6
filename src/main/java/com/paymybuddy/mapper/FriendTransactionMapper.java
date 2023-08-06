package com.paymybuddy.mapper;

import com.paymybuddy.dto.FriendTransactionDisplayDTO;
import com.paymybuddy.model.FriendTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FriendTransactionMapper {

    @Mapping(source="sender.firstName", target = "name")
    @Mapping(source="description", target = "description")
    @Mapping(source="amount", target = "amount")
    @Mapping(source="createdAt", target = "createdAt")
    FriendTransactionDisplayDTO toFriendTransactionDisplayDTO(FriendTransaction transaction);
}
