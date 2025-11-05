package com.project.edusync.finance.service.implementation;

import com.project.edusync.common.exception.finance.FeeStructureNotFoundException;
import com.project.edusync.finance.dto.configuration.LateFeeRuleCreateDTO;
import com.project.edusync.finance.dto.configuration.LateFeeRuleResponseDTO;
import com.project.edusync.finance.mapper.LateFeeRuleMapper;
import com.project.edusync.finance.model.entity.FeeStructure;
import com.project.edusync.finance.model.entity.LateFeeRule;
import com.project.edusync.finance.repository.FeeStructureRepository;
import com.project.edusync.finance.repository.LateFeeRuleRepository;
import com.project.edusync.finance.service.LateFeeRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LateFeeRuleServiceImpl implements LateFeeRuleService {

    private final LateFeeRuleRepository lateFeeRuleRepository;
    private final FeeStructureRepository feeStructureRepository;
    private final LateFeeRuleMapper lateFeeRuleMapper;
    // We don't inject ModelMapper here as we are using the mapper class

    @Override
    @Transactional
    public LateFeeRuleResponseDTO createLateFeeRule(LateFeeRuleCreateDTO createDTO) {

        // 1. Manually create the entity (to handle optional relations)
        LateFeeRule lateFeeRule = new LateFeeRule();
        lateFeeRule.setRuleName(createDTO.getRuleName());
        lateFeeRule.setDaysAfterDue(createDTO.getDaysAfterDue());
        lateFeeRule.setFineType(createDTO.getFineType());
        lateFeeRule.setFineValue(createDTO.getFineValue());
        lateFeeRule.setActive(createDTO.isActive());

        // 2. Handle the optional relationship to FeeStructure
        if (createDTO.getStructureId() != null) {
            FeeStructure feeStructure = feeStructureRepository.findById(createDTO.getStructureId())
                    .orElseThrow(() -> new FeeStructureNotFoundException("Fee structure not found for structure ID: " + createDTO.getStructureId()));
            lateFeeRule.setFeeStructure(feeStructure);
        }
        // If structureId is null, the feeStructure field remains null (global rule)

        // 3. Save the new entity
        LateFeeRule savedRule = lateFeeRuleRepository.save(lateFeeRule);

        // 4. Map to response DTO and return
        return lateFeeRuleMapper.toDto(savedRule);
    }
}