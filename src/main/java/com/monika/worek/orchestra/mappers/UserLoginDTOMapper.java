package com.monika.worek.orchestra.mappers;

import com.monika.worek.orchestra.dto.UserLoginDTO;
import com.monika.worek.orchestra.model.User;

public class UserLoginDTOMapper {

    public static UserLoginDTO mapToDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserLoginDTO(
                user.getEmail(),
                user.getPassword(),
                user.getRoles()
        );
    }
}
