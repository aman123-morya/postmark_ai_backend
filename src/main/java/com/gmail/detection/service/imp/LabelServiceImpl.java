package com.gmail.detection.service.imp;

import com.gmail.detection.dto.LabelDTO;
import com.gmail.detection.entity.Label;
import com.gmail.detection.exception.DuplicateResourceException;
import com.gmail.detection.exception.ResourceNotFoundException;
import com.gmail.detection.repository.LabelRepository;
import com.gmail.detection.service.LabelService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;

    public LabelServiceImpl(LabelRepository labelRepository) {
        this.labelRepository = labelRepository;
    }

    @Override
    public LabelDTO createLabel(LabelDTO labelDTO) {

        if (labelRepository.existsByLabelName(labelDTO.getLabelName())) {
            throw new DuplicateResourceException("Label already exists.");
        }

        Label label = new Label();
        label.setLabelName(labelDTO.getLabelName());
        label.setDescription(labelDTO.getDescription());

        Label saved = labelRepository.save(label);

        return convertToDTO(saved);
    }

    @Override
    public LabelDTO updateLabel(Long id, LabelDTO labelDTO) {

        Label label = labelRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Label not found : " + id));

        label.setLabelName(labelDTO.getLabelName());
        label.setDescription(labelDTO.getDescription());

        return convertToDTO(labelRepository.save(label));
    }

    @Override
    public LabelDTO getLabelById(Long id) {

        Label label = labelRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Label not found : " + id));

        return convertToDTO(label);
    }

    @Override
    public List<LabelDTO> getAllLabels() {

        return labelRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteLabel(Long id) {

        Label label = labelRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Label not found : " + id));

        labelRepository.delete(label);
    }

    private LabelDTO convertToDTO(Label label) {

        LabelDTO dto = new LabelDTO();

        dto.setId(label.getId());
        dto.setLabelName(label.getLabelName());
        dto.setDescription(label.getDescription());

        return dto;
    }
}