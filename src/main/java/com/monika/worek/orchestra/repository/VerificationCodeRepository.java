package com.monika.worek.orchestra.repository;

import com.monika.worek.orchestra.model.VerificationCode;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationCodeRepository extends CrudRepository<VerificationCode, Long> {
    VerificationCode findByEmail(String userEmail);
}