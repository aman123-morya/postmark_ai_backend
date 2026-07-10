package com.gmail.detection.repository;

import com.gmail.detection.entity.Label;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LabelRepository extends JpaRepository<Label, Long> {

    Optional<Label> findByLabelName(String labelName);

    boolean existsByLabelName(String labelName);

}