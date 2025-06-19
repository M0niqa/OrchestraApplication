package com.monika.worek.orchestra.mapper;

import com.monika.worek.orchestra.dto.UserLoginDTO;
import com.monika.worek.orchestra.dtoMappers.UserLoginDTOMapper;
import com.monika.worek.orchestra.model.User;
import com.monika.worek.orchestra.model.UserRole;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserLoginDTOMapperTest {

    @Test
    void mapToDto_whenGivenNullUser_thenShouldReturnNull() {
        // given
        User user = null;

        // when
        UserLoginDTO dto = UserLoginDTOMapper.mapToDto(user);

        // then
        assertThat(dto).isNull();
    }

    @Test
    void mapToDto_whenUserHasNullFields_thenDtoFieldsShouldAlsoBeNull() {
        // given
        User userWithNullFields = User.builder()
                .email(null)
                .password(null)
                .roles(null)
                .build();

        // when
        UserLoginDTO dto = UserLoginDTOMapper.mapToDto(userWithNullFields);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getEmail()).isNull();
        assertThat(dto.getPassword()).isNull();
        assertThat(dto.getRoles()).isNull();
    }

    @Test
    void mapToDto_whenGivenValidUser_thenShouldMapAllFields() {
        // given
        UserRole musicianRole = new UserRole(1L, "MUSICIAN");
        User user = User.builder()
                .email("monika@example.com")
                .password("encodedPassword123!")
                .roles(Set.of(musicianRole))
                .build();

        // when
        UserLoginDTO dto = UserLoginDTOMapper.mapToDto(user);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getEmail()).isEqualTo(user.getEmail());
        assertThat(dto.getPassword()).isEqualTo(user.getPassword());
        assertThat(dto.getRoles()).isEqualTo(user.getRoles());
    }
}