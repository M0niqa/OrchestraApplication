package com.monika.worek.orchestra.auth;

import com.monika.worek.orchestra.dto.UserDTO;
import com.monika.worek.orchestra.model.User;

public class UserDTOMapper {

    public static UserDTO mapToDto(User user) {
        return new UserDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword(),
                user.getRoles()
        );
    }
}
