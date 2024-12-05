package com.youcode.ebanking.repository;

import com.youcode.ebanking.model.EbUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<EbUser, Long> {
    Optional<EbUser> findByUsername(String username);
    boolean existsEbUserByUsername(String email);
}