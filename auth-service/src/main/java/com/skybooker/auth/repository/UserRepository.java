package com.skybooker.auth.repository;

import com.skybooker.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUserId(int userId);

    boolean existsByEmail(String email);

    List<User> findAllByRole(String role);

    Optional<User> findByPhone(String phone);

    Optional<User> findByPassportNumber(String passportNumber);

    void deleteByUserId(int userId);
}