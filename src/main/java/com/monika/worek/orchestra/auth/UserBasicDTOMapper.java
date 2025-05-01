package com.monika.worek.orchestra.auth;

import com.monika.worek.orchestra.dto.UserBasicDTO;
import com.monika.worek.orchestra.dto.UserDTO;
import com.monika.worek.orchestra.model.User;

public class UserBasicDTOMapper {

    public static UserBasicDTO mapToBasicDto(UserDTO user) {
        return new UserBasicDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName()
        );
    }

    public static UserBasicDTO mapToBasicDto(User user) {
        return new UserBasicDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName()
        );
    }

}
