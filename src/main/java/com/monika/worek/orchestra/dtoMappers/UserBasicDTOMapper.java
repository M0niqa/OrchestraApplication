package com.monika.worek.orchestra.dtoMappers;

import com.monika.worek.orchestra.dto.UserBasicDTO;
import com.monika.worek.orchestra.model.User;
import com.monika.worek.orchestra.model.UserRole;

import java.util.stream.Collectors;

public class UserBasicDTOMapper {

    public static UserBasicDTO mapToBasicDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserBasicDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoles().stream().map(UserRole::getName).collect(Collectors.toSet())
        );
    }
}
