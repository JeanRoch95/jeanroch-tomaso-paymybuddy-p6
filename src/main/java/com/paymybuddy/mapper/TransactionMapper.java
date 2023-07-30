package com.paymybuddy.mapper;

import com.paymybuddy.dto.TransactionDTO;
import com.paymybuddy.model.BankTransfer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

        @Mapping(source="bankAccount.name", target="name" )
        @Mapping(source="description", target="description")
        @Mapping(target="amount", expression = "java(-(toBank.getAmount()))")
        TransactionDTO debitFromBankTransfer(BankTransfer toBank);

        @Mapping(source="bankAccount.name", target="name")
        @Mapping(source="description", target="description")
        @Mapping(target="amount", expression = "java(fromBank.getAmount())")
        TransactionDTO creditFromBankTransfer(BankTransfer fromBank);

}
