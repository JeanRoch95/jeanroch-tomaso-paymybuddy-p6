package com.paymybuddy.mapper;

import com.paymybuddy.dto.FriendTransactionDisplayDTO;
import com.paymybuddy.model.FriendTransaction;
import com.paymybuddy.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class FriendTransactionMapperTest {

    private final FriendTransactionMapper mapper = Mappers.getMapper(FriendTransactionMapper.class);

    @Test
    public void testToFriendTransactionDisplayDTO() {
        User sender = new User();
        sender.setFirstName("John");

        FriendTransaction transaction = new FriendTransaction();
        transaction.setSender(sender);
        transaction.setDescription("Payment for dinner");
        transaction.setAmount(BigDecimal.valueOf(50.0));

        FriendTransactionDisplayDTO dto = mapper.toFriendTransactionDisplayDTO(transaction);

        assertEquals("John", dto.getName());
        assertEquals("Payment for dinner", dto.getDescription());
        assertEquals(BigDecimal.valueOf(50.0), dto.getAmount());
    }
}
