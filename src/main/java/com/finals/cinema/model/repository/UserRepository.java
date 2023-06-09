package com.finals.cinema.model.repository;

import com.finals.cinema.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findByEmail(String email);
    User findByUsername(String username);
    List<User> findAll();

}
