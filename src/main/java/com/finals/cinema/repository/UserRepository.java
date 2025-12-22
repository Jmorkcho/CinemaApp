package com.finals.cinema.repository;

import com.finals.cinema.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Boolean existsByEmailOrUsername(String email, String username);

    List<User> findAll();

    Optional<User> findByUsername(String username);
}