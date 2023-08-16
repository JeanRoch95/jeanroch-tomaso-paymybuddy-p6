package com.paymybuddy.service;

import com.paymybuddy.dto.BankAccountDTO;
import com.paymybuddy.dto.UserConnectionInformationDTO;
import com.paymybuddy.dto.UserDTO;
import com.paymybuddy.dto.UserInformationDTO;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {

    UserDTO getCurrentAccount();

    BigDecimal getCurrentUserBalance();

    List<UserConnectionInformationDTO> getAllConnectionByCurrentAccount();

    Iterable<BankAccountDTO> getBankAccountByCurrentUserId();

    UserInformationDTO getCurrentAccountInformations();

    Boolean checkIfEmailChanged(UserInformationDTO userInformationDTO);

    void updateCurrentUserInformation(UserInformationDTO userInformationDTO);

}
