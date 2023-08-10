package com.paymybuddy.mapper;

import com.paymybuddy.dto.UserConnectionInformationDTO;
import com.paymybuddy.model.User;
import com.paymybuddy.model.UserConnection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
@ExtendWith(MockitoExtension.class)
public class UserConnectionMapperTest {

    private final UserConnectionMapper mapper = Mappers.getMapper(UserConnectionMapper.class);

    @Test
    public void testGetFriendConnectionList() {
        User receiver = new User();
        receiver.setFirstName("John");
        receiver.setEmail("john.doe@example.com");

        UserConnection userConnection = new UserConnection();
        userConnection.setReceiver(receiver);

        UserConnectionInformationDTO dto = mapper.getFriendConnectionList(userConnection);

        assertThat(dto.getName()).isEqualTo("John");
        assertThat(dto.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    public void testGetFriendNameConnectionList() {
        User receiver = new User();
        receiver.setFirstName("John");
        receiver.setId(1L);

        UserConnection userConnection = new UserConnection();
        userConnection.setReceiver(receiver);

        UserConnectionInformationDTO dto = mapper.getFriendNameConnectionList(userConnection);

        assertThat(dto.getName()).isEqualTo("John");
        assertThat(dto.getReceiverUserId()).isEqualTo(1L);
    }
}
