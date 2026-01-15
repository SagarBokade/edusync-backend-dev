package com.project.edusync.uis.mapper;

import com.project.edusync.iam.model.dto.CreateLibrarianRequestDTO;
import com.project.edusync.uis.model.entity.details.LibrarianDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {JsonMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LibrarianMapper {

    @Mapping(target = "mlisDegree", source = "hasMlisDegree")
    // JsonMapper handles List<String> -> String (JSON)
    @Mapping(target = "librarySystemPermissions", source = "librarySystemPermissions")
    LibrarianDetails toEntity(CreateLibrarianRequestDTO dto);
}