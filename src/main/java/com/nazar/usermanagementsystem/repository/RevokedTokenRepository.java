package com.nazar.usermanagementsystem.repository;

import com.nazar.usermanagementsystem.entity.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RevokedTokenRepository extends JpaRepository<RevokedToken, Long> {

    boolean existsByToken(String token);

}