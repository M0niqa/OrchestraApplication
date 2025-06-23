package com.monika.worek.orchestra.repository;

import com.monika.worek.orchestra.model.Token;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TokenRepository extends CrudRepository<Token, Long> {

    Optional<Token> findByToken(String token);
    void deleteByToken(String token);
    void deleteByEmail(String email);
}

