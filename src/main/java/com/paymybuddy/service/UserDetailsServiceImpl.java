package com.paymybuddy.service;

import com.paymybuddy.exceptions.UserNotFoundException;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByEmail(String email) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UserNotFoundException("Aucun utilisateur trouvé avec l'email " + email);
        }

        return user;
    }
}
