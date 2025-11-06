package com.project.edusync.finance.mapper;

import com.project.edusync.finance.dto.configuration.LateFeeRuleResponseDTO;
import com.project.edusync.finance.model.entity.LateFeeRule;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LateFeeRuleMapper {

    private final ModelMapper modelMapper;

    /**
     * Converts a LateFeeRule entity to its Response DTO.
     */
    public LateFeeRuleResponseDTO toDto(LateFeeRule lateFeeRule) {
        // 1. Use ModelMapper for all flat fields
        LateFeeRuleResponseDTO dto = modelMapper.map(lateFeeRule, LateFeeRuleResponseDTO.class);

        // 2. Manually map the structureId from the nested object
        if (lateFeeRule.getFeeStructure() != null) {
            dto.setStructureId(lateFeeRule.getFeeStructure().getId());
        }

        return dto;
    }
}