package com.project.edusync.uis.mapper;

import com.project.edusync.uis.model.dto.profile.AddressDTO;
import com.project.edusync.uis.model.entity.UserAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for converting Address-related entities to DTOs.
 * <p>
 * This handles the complexity of the {@link UserAddress} join table,
 * which contains metadata (like "HOME" or "MAILING") separate from the
 * actual {@link com.project.edusync.uis.model.entity.Address} data.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface AddressMapper {

    /**
     * Flattens the nested Address entity into a single DTO.
     *
     * @param userAddress The relationship entity containing the address type and link to the actual address.
     * @return A unified AddressDTO containing both metadata and address details.
     */
    @Mapping(source = "address.id", target = "id")
    @Mapping(source = "address.addressLine1", target = "addressLine1")
    @Mapping(source = "address.addressLine2", target = "addressLine2")
    @Mapping(source = "address.city", target = "city")
    @Mapping(source = "address.stateProvince", target = "state")
    @Mapping(source = "address.postalCode", target = "postalCode")
    @Mapping(source = "address.country", target = "country")
    // 'addressType' matches by name automatically between Entity and DTO
    AddressDTO toDto(UserAddress userAddress);
}