package com.paymybuddy.service;

import com.paymybuddy.dto.UserConnectionDTO;
import com.paymybuddy.dto.UserConnectionInformationDTO;
import com.paymybuddy.model.User;
import com.paymybuddy.model.UserConnection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserConnectionService {

    UserConnectionDTO addUserConnection(UserConnectionInformationDTO userConnectionInformationDTO);

    List<UserConnectionInformationDTO> getAllConnectionByCurrentAccount();

    Page<UserConnectionInformationDTO> getFriendConnectionList(Pageable pageable);

}
