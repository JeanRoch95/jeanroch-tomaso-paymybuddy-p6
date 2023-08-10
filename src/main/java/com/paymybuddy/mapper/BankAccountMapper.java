package com.paymybuddy.mapper;

import com.paymybuddy.dto.BankAccountDTO;
import com.paymybuddy.model.BankAccount;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BankAccountMapper {

    BankAccountDTO toDTO(BankAccount bankAccount);

}
