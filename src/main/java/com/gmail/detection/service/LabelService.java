package com.gmail.detection.service;

import com.gmail.detection.dto.LabelDTO;

import java.util.List;

public interface LabelService {

    LabelDTO createLabel(LabelDTO labelDTO);

    LabelDTO updateLabel(Long id, LabelDTO labelDTO);

    LabelDTO getLabelById(Long id);

    List<LabelDTO> getAllLabels();

    void deleteLabel(Long id);

}