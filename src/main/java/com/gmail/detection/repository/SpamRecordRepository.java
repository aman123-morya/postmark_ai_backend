package com.gmail.detection.repository;

import com.gmail.detection.entity.SpamRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpamRecordRepository extends JpaRepository<SpamRecord, Long> {

    List<SpamRecord> findByBlocked(boolean blocked);

    List<SpamRecord> findBySender(String sender);

}