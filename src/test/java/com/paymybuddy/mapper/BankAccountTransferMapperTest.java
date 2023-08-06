package com.paymybuddy.mapper;

import com.paymybuddy.dto.BankTransferInformationDTO;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.BankTransfer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
public class BankAccountTransferMapperTest {

    private final BankAccountTransferMapper mapper = Mappers.getMapper(BankAccountTransferMapper.class);

    @Test
    public void testDebitFromBankTransfer() {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setName("John");

        BankTransfer toBank = new BankTransfer();
        toBank.setBankAccount(bankAccount);
        toBank.setDescription("Debit Test");
        toBank.setAmount(100.0);

        BankTransferInformationDTO dto = mapper.debitFromBankTransfer(toBank);

        assertThat(dto.getName()).isEqualTo("John");
        assertThat(dto.getDescription()).isEqualTo("Debit Test");
        assertThat(dto.getAmount()).isEqualTo(-100.0);
    }

    @Test
    public void testCreditFromBankTransfer() {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setName("John");

        BankTransfer fromBank = new BankTransfer();
        fromBank.setBankAccount(bankAccount);
        fromBank.setDescription("Credit Test");
        fromBank.setAmount(100.0);

        BankTransferInformationDTO dto = mapper.creditFromBankTransfer(fromBank);

        assertThat(dto.getName()).isEqualTo("John");
        assertThat(dto.getDescription()).isEqualTo("Credit Test");
        assertThat(dto.getAmount()).isEqualTo(100.0);
    }

}
