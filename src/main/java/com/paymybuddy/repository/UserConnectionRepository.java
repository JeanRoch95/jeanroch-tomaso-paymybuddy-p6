package com.paymybuddy.repository;

import com.paymybuddy.dto.UserConnectionDTO;
import com.paymybuddy.model.User;
import com.paymybuddy.model.UserConnection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserConnectionRepository extends CrudRepository<UserConnection, Long> {

    Page<UserConnection> findBySenderOrderByCreatedAtDesc(User user, Pageable pageable);

    List<UserConnection> findUserConnectionBySender(User sender);

    Optional<UserConnection> findBySenderAndReceiver(User sender, User receiver);
}
