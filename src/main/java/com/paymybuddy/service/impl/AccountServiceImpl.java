package com.paymybuddy.service.impl;

import com.paymybuddy.dto.*;
import com.paymybuddy.exceptions.UserNotFoundException;
import com.paymybuddy.exceptions.WrongPasswordException;
import com.paymybuddy.mapper.UserConnectionMapper;
import com.paymybuddy.mapper.UserMapper;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.BankAccountRepository;
import com.paymybuddy.repository.UserConnectionRepository;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.AccountService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class AccountServiceImpl implements AccountService {

    private final UserRepository userRepository;

    private final UserConnectionRepository userConnectionRepository;

    private final UserMapper userMapper;

    private final UserConnectionMapper userConnectionMapper;

    private final BankAccountRepository bankAccountRepository;

    private final PasswordEncoder passwordEncoder;


    public AccountServiceImpl(UserRepository userRepository, UserConnectionRepository userConnectionRepository, UserMapper userMapper, UserConnectionMapper userConnectionMapper, BankAccountRepository bankAccountRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userConnectionRepository = userConnectionRepository;
        this.userMapper = userMapper;
        this.userConnectionMapper = userConnectionMapper;
        this.bankAccountRepository = bankAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDTO getCurrentAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        return userMapper.toDTO(user);
    }

    @Override
    public BigDecimal getCurrentUserBalance() {
        Optional<User> user = userRepository.findById(getCurrentAccount().getId().intValue());
        BigDecimal currentUserBalance = user.map(User::getBalance).orElse(BigDecimal.valueOf(0.0));
        return currentUserBalance;
    }

    @Override
    public List<UserConnectionInformationDTO> getAllConnectionByCurrentAccount() {
        Optional<User> currentUser = userRepository.findById(getCurrentAccount().getId().intValue());
        return userConnectionRepository.findUserConnectionBySender(currentUser.get()).stream()
                .map(userConnectionMapper::getFriendNameConnectionList)
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<BankAccountDTO> getBankAccountByCurrentUserId() {
        Iterable<BankAccount> bankList = bankAccountRepository.findByUserId(getCurrentAccount().getId().intValue());

        List<BankAccountDTO> bankAccountDtoList = StreamSupport.stream(bankList.spliterator(), false)
                .map(bankAccount -> new BankAccountDTO(bankAccount))
                .collect(Collectors.toList());

        return bankAccountDtoList;
    }

    @Override
    public UserInformationDTO getCurrentAccountInformations() {

        UserDTO userDTO = getCurrentAccount();

        UserInformationDTO userInformationDTO = new UserInformationDTO();
        userInformationDTO.setFirstName(userDTO.getFirstName());
        userInformationDTO.setLastName(userDTO.getLastName());
        userInformationDTO.setEmail(userDTO.getEmail());

        return userInformationDTO;
    }

    @Override
    public Boolean checkIfEmailChanged(UserInformationDTO userDto) {
        String currentEmail = getCurrentAccount().getEmail();
        return !currentEmail.equals(userDto.getEmail());
    }

    @Override
    public void updateCurrentAccountInformation(UserInformationDTO userInformationDTO) {

        UserDTO userDTO = getCurrentAccount();

        userDTO.setFirstName(userInformationDTO.getFirstName());
        userDTO.setLastName(userInformationDTO.getLastName());
        userDTO.setEmail(userInformationDTO.getEmail());
        userDTO.setUpdatedAt(Instant.now());

        userRepository.save(userMapper.fromDTO(userDTO));
    }

    @Override
    public void updateCurrentAccountPassword(PasswordDTO passwordDTO) {
        User user = userMapper.fromDTO(getCurrentAccount());

        if(user == null) {
            throw new UserNotFoundException("L'utilisateur actuel n'existe pas.");
        }

        if(!passwordEncoder.matches(passwordDTO.getCurrentPassword(), user.getPassword())) {
            throw new WrongPasswordException("Le mot de passe entré ne correspond pas avec votre mot de passe actuel");
        }

        String encodedPassword = passwordEncoder.encode(passwordDTO.getNewPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);
    }

}
