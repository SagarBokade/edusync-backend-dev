package com.project.edusync.iam.model.dto;

import com.project.edusync.uis.model.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateUserRequestDTO {

    // --- Identity (User Entity) ---
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    // Optional: If not provided, system generates a random password
    private String initialPassword;

    // --- Personal Profile (UserProfile Entity) ---
    @NotBlank(message = "First name is required")
    private String firstName;

    private String middleName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String preferredName;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    private Gender gender; // Assuming you have this Enum

    @Size(max = 5000, message = "Bio is too long")
    private String bio;
}