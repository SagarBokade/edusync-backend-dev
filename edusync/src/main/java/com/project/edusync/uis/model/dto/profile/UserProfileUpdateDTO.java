package com.project.edusync.uis.model.dto.profile;

import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UserProfileUpdateDTO {

    @Size(min = 1, max = 50)
    private String firstName;

    @Size(min = 1, max = 50)
    private String lastName;

    private String preferredName;
    private LocalDate dateOfBirth;

    @Size(max = 20)
    private String contactPhone;

    @Size(max = 5000)
    private String bio;

    private String gender;
}