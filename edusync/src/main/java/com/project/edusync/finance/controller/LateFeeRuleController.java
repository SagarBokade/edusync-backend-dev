package com.project.edusync.finance.controller;

import com.project.edusync.finance.dto.configuration.LateFeeRuleCreateDTO;
import com.project.edusync.finance.dto.configuration.LateFeeRuleResponseDTO;
import com.project.edusync.finance.service.LateFeeRuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.url}/auth/finance/late-fee-rules") // Base path
@RequiredArgsConstructor
public class LateFeeRuleController {

    private final LateFeeRuleService lateFeeRuleService;

    /**
     * POST /api/v1/finance/late-fee-rules
     * Creates a new late fee rule.
     */
    @PostMapping
    public ResponseEntity<LateFeeRuleResponseDTO> createLateFeeRule(
            @Valid @RequestBody LateFeeRuleCreateDTO createDTO) {

        LateFeeRuleResponseDTO response = lateFeeRuleService.createLateFeeRule(createDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
