package com.project.edusync.uis.mapper;

import com.project.edusync.iam.model.dto.CreateTeacherRequestDTO;
import com.project.edusync.uis.model.entity.details.TeacherDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {JsonMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeacherMapper {

    @Mapping(target = "stateLicenseNumber", source = "stateLicenseNumber")
    @Mapping(target = "educationLevel", source = "educationLevel")
    @Mapping(target = "yearsOfExperience", source = "yearsOfExperience")
    // JsonMapper handles List<String> -> String (JSON) automatically
    @Mapping(target = "specializations", source = "specializations")
    @Mapping(target = "certifications", source = "certifications")
    TeacherDetails toEntity(CreateTeacherRequestDTO dto);
}