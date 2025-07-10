package com.qualityinspection.swequalityinspection.model.mappers;

import com.qualityinspection.swequalityinspection.model.responseDto.UserResponseDto;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mappings({
            @Mapping(target = "userId", source = "id"),
            @Mapping(target = "checklistFilled", constant = "0"),
            @Mapping(target = "defectReportCreated", constant = "0"),
            @Mapping(target = "role", constant = "")
    })
    UserResponseDto toUserResponseDto(UserRepresentation user);
}
