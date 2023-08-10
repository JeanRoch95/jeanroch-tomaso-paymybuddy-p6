package com.paymybuddy.mapper;

import com.paymybuddy.dto.BankAccountDTO;
import com.paymybuddy.model.BankAccount;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BankAccountMapperTest {

    private final BankAccountMapper mapper = Mappers.getMapper(BankAccountMapper.class);

    @Test
    public void testToDTO() {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setName("nameTest");

        BankAccountDTO dto = mapper.toDTO(bankAccount);

        assertEquals(bankAccount.getName(), dto.getName());
    }
}
