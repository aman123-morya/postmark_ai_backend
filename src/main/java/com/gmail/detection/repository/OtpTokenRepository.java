package com.gmail.detection.repository;

import com.gmail.detection.entity.OtpToken;
import com.gmail.detection.enums.OtpPurpose;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {

    Optional<OtpToken> findTopByEmailAndPurposeAndUsedFalseOrderByCreatedAtDesc(String email, OtpPurpose purpose);
}
