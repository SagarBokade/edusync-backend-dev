package com.project.edusync.finance.mapper;

import com.project.edusync.finance.dto.scholarship.ScholarshipAssignmentDTO;
import com.project.edusync.finance.dto.scholarship.ScholarshipTypeDTO;
import com.project.edusync.finance.model.entity.ScholarshipAssignment;
import com.project.edusync.finance.model.entity.ScholarshipType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ScholarshipMapper {
    ScholarshipTypeDTO toDto(ScholarshipType type);
    
    @Mapping(target = "scholarshipId", source = "scholarshipType.id")
    @Mapping(target = "scholarshipName", source = "scholarshipType.name")
    ScholarshipAssignmentDTO toDto(ScholarshipAssignment assignment);
}
