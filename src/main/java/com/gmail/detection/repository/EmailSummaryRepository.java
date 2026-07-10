package com.gmail.detection.repository;

import com.gmail.detection.entity.EmailSummary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailSummaryRepository
        extends JpaRepository<EmailSummary, Long> {

}