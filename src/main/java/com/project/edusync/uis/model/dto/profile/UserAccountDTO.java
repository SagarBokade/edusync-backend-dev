package com.project.edusync.uis.model.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAccountDTO {
    private Long userId;
    private String username;
    private String email;
    private boolean isActive;
    private LocalDateTime lastLoginTimestamp;
}
