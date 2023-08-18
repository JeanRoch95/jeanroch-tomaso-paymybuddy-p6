package com.paymybuddy.mapper;

import com.paymybuddy.dto.BankTransferInformationDTO;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.BankTransfer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
public class BankAccountTransferMapperTest {

    private final BankAccountTransferMapper mapper = Mappers.getMapper(BankAccountTransferMapper.class);

    @Test
    void testMapBankTransfer() {
        BankTransfer bankTransfer = new BankTransfer();
        BankAccount bankAccount = new BankAccount();
        bankAccount.setName("Test Bank");
        bankTransfer.setBankAccount(bankAccount);
        bankTransfer.setDescription("Test Description");
        bankTransfer.setAmount(BigDecimal.valueOf(100.0));

        BankTransferInformationDTO dto = mapper.mapBankTransfer(bankTransfer);

        assertEquals("Test Bank", dto.getName());
        assertEquals("Test Description", dto.getDescription());
        assertEquals(BigDecimal.valueOf(100.0), dto.getAmount());
    }

}
