package com.paymybuddy.mapper;

import com.paymybuddy.dto.BankTransferInformationDTO;
import com.paymybuddy.dto.UserConnectionInformationDTO;
import com.paymybuddy.model.BankTransfer;
import com.paymybuddy.model.UserConnection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

        @Mapping(source="bankAccount.name", target="name" )
        @Mapping(source="description", target="description")
        @Mapping(target="amount", expression = "java(-(toBank.getAmount()))")
        BankTransferInformationDTO debitFromBankTransfer(BankTransfer toBank);

        @Mapping(source="bankAccount.name", target="name")
        @Mapping(source="description", target="description")
        @Mapping(target="amount", expression = "java(fromBank.getAmount())")
        BankTransferInformationDTO creditFromBankTransfer(BankTransfer fromBank);



}
