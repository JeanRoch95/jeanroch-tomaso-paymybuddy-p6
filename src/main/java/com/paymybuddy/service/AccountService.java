package com.paymybuddy.service;

import com.paymybuddy.dto.*;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {

    UserDTO getCurrentAccount();

    Iterable<BankAccountDTO> getBankAccountByCurrentUserId();

    UserInformationDTO getCurrentAccountInformations();

    Boolean checkIfEmailChanged(UserInformationDTO userInformationDTO);

    void updateCurrentAccountInformation(UserInformationDTO userInformationDTO);

    void updateCurrentAccountPassword(PasswordDTO passwordDTO);

}
