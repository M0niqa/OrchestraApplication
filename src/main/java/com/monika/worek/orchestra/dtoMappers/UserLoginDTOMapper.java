package com.monika.worek.orchestra.dtoMappers;

import com.monika.worek.orchestra.dto.UserLoginDTO;
import com.monika.worek.orchestra.model.User;

public class UserLoginDTOMapper {

    public static UserLoginDTO mapToDto(User user) {
        return new UserLoginDTO(
                user.getEmail(),
                user.getPassword(),
                user.getRoles()
        );
    }
}
