package com.ledgerlite.ledgerliteapp.owner;

import org.mapstruct.*;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface OwnerMapper {

    OwnerDTO toDto(Owner owner);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Owner toEntity(CreateOwnerRequest request);

    Owner toEntity(UpdateOwnerRequest updateOwnerRequest);

    @InheritInverseConfiguration(name = "toEntity")
    UpdateOwnerRequest toUpdateOwnerRequest(Owner owner);

    Owner toEntity(OwnerDTO ownerDTO);

    @InheritConfiguration(name = "toEntity")
    Owner updateWithNull(OwnerDTO ownerDTO, @MappingTarget Owner owner);
}
