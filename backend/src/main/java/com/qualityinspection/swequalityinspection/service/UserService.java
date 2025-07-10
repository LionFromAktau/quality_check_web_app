package com.qualityinspection.swequalityinspection.service;

import com.qualityinspection.swequalityinspection.config.KeycloakConfig;
import com.qualityinspection.swequalityinspection.exceptions.ExceptionMessages;
import com.qualityinspection.swequalityinspection.model.mappers.UserMapper;
import com.qualityinspection.swequalityinspection.model.responseDto.UserResponseDto;
import com.qualityinspection.swequalityinspection.repository.ChecklistResultRepository;
import com.qualityinspection.swequalityinspection.repository.DefectReportRepository;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService {

    private final Keycloak keycloak;
    private final ChecklistResultRepository checklistResultRepository;
    private final DefectReportRepository defectReportRepository;
    private final UserMapper userMapper;

    public UserService(Keycloak keycloak, ChecklistResultRepository checklistResultRepository, DefectReportRepository defectReportRepository, UserMapper userMapper) {
        this.keycloak = keycloak;
        this.checklistResultRepository = checklistResultRepository;
        this.defectReportRepository = defectReportRepository;
        this.userMapper = userMapper;
    }

    public UserResponseDto getUserById(String userId) {
        try {
            UserRepresentation user = keycloak.realm(KeycloakConfig.OAUTH2REALM)
                    .users()
                    .get(userId)
                    .toRepresentation();

            // 🔑 Fetch user's realm roles
            List<RoleRepresentation> roleRepresentations = keycloak.realm(KeycloakConfig.OAUTH2REALM)
                    .users()
                    .get(userId)
                    .roles()
                    .realmLevel()
                    .listAll();

            // Convert to list of role names
            List<String> roles = roleRepresentations.stream()
                    .map(RoleRepresentation::getName)
                    .toList();

            // OR if you want only the first role (not recommended for multirole users)
            String role = roles.isEmpty() ? null : roles.get(0);

            // Count checklist results and defect reports
            int checklistsFilled = checklistResultRepository.countByUserId(userId);
            int defectReportsCreated = defectReportRepository.countByUserId(userId);

            // Map to DTO without role first
            UserResponseDto userResponseDto = userMapper.toUserResponseDto(user);

            // Return DTO with role(s)
            return new UserResponseDto(
                    userResponseDto.userId(),
                    userResponseDto.username(),
                    userResponseDto.email(),
                    userResponseDto.firstName(),
                    userResponseDto.lastName(),
                    role,
                    checklistsFilled,
                    defectReportsCreated
            );

        } catch (javax.ws.rs.NotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, ExceptionMessages.UserNotFound
            );
        }
    }
}
