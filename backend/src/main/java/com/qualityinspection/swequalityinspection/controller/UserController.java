package com.qualityinspection.swequalityinspection.controller;

import com.qualityinspection.swequalityinspection.config.KeycloakConfig;
import com.qualityinspection.swequalityinspection.model.entities.ChecklistResultEntity;
import com.qualityinspection.swequalityinspection.model.entities.DefectReportEntity;
import com.qualityinspection.swequalityinspection.model.responseDto.UserResponseDto;
import com.qualityinspection.swequalityinspection.repository.ChecklistResultRepository;
import com.qualityinspection.swequalityinspection.repository.DefectReportRepository;
import com.qualityinspection.swequalityinspection.service.UserService;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService, ChecklistResultRepository checklistResultRepository, DefectReportRepository defectReportRepository) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserResponseDto getUserInfo(@AuthenticationPrincipal OidcUser oidcUser) {
        return userService.getUserById(oidcUser.getSubject());
    }

    @GetMapping("/{userId}")
    public UserResponseDto getUserById(@PathVariable String userId) {
        return userService.getUserById(userId);
    }

}
