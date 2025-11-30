package com.project.edusync.iam.service.impl;

import com.project.edusync.iam.repository.UserRepository;
import com.project.edusync.iam.service.IAMRoleService;
import com.project.edusync.common.exception.ResourceNotFoundException; // Assuming this is common
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class IAMRoleServiceImpl implements IAMRoleService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Set<String> getRoleNamesByUserId(Long userId) {
        // Fetch the User entity, which should eagerly load or be configured to fetch
        // the associated Roles collection efficiently.
        Set<String> roles = userRepository.findRoleNamesByUserId(userId);

        if (roles.isEmpty()) {
            log.error("User {} has no roles", userId);
            throw new ResourceNotFoundException("User : " + userId + " has no roles");
        }

        return roles.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toSet());
    }
}