package com.paymybuddy.mapper;

import com.paymybuddy.dto.BankTransferInformationDTO;
import com.paymybuddy.model.BankTransfer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BankAccountTransferMapper {

        @Mapping(source="bankAccount.name", target="name")
        @Mapping(source="description", target="description")
        @Mapping(target="amount", expression = "java(bankTransfer.getAmount())") // un mappage standard sans modification
        BankTransferInformationDTO mapBankTransfer(BankTransfer bankTransfer);
}
