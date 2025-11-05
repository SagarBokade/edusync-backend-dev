package com.project.edusync.finance.service;

import com.project.edusync.finance.dto.configuration.LateFeeRuleCreateDTO;
import com.project.edusync.finance.dto.configuration.LateFeeRuleResponseDTO;

/**
 * Service interface for managing Late Fee Rules.
 */
public interface LateFeeRuleService {

    /**
     * Creates a new late fee rule.
     *
     * @param createDTO The DTO containing the rule details.
     * @return The response DTO of the newly created rule.
     */
    LateFeeRuleResponseDTO createLateFeeRule(LateFeeRuleCreateDTO createDTO);

    // We will add get, list, and update methods later.
}