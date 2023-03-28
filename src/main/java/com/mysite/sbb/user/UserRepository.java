package com.mysite.sbb.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<SiteUser, Long> {
    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE site_user AUTO_INCREMENT = 1", nativeQuery = true)
    void clearAutoIncrement();

    Optional<SiteUser> findByUsername(String username);
}