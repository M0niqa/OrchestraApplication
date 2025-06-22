package com.monika.worek.orchestra.mapper;

import com.monika.worek.orchestra.dto.UserBasicDTO;
import com.monika.worek.orchestra.mappers.UserBasicDTOMapper;
import com.monika.worek.orchestra.model.User;
import com.monika.worek.orchestra.model.UserRole;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class UserBasicDTOMapperTest {

    @Test
    void mapToBasicDto_whenGivenNullUser_thenShouldReturnNull() {
        // given
        User user = null;

        // when
        UserBasicDTO dto = UserBasicDTOMapper.mapToBasicDto(user);

        // then
        assertThat(dto).isNull();
    }

    @Test
    void mapToBasicDto_whenUserHasNullRoles_thenShouldThrowException() {
        // given
        User user = User.builder()
                .id(1L)
                .firstName("Test")
                .lastName("User")
                .roles(null)
                .build();

        // when
        // then
        assertThatThrownBy(() -> UserBasicDTOMapper.mapToBasicDto(user))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void mapToBasicDto_whenUserHasEmptyRoles_thenShouldReturnDtoWithEmptyRoleSet() {
        // given
        User user = User.builder()
                .id(1L)
                .firstName("Test")
                .lastName("User")
                .roles(Collections.emptySet())
                .build();

        // when
        UserBasicDTO dto = UserBasicDTOMapper.mapToBasicDto(user);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getRoles()).isNotNull().isEmpty();
    }

    @Test
    void mapToBasicDto_whenGivenValidUser_thenShouldMapAllFields() {
        // given
        UserRole musicianRole = new UserRole(1L, "MUSICIAN");
        UserRole inspectorRole = new UserRole(2L, "INSPECTOR");

        User user = User.builder()
                .id(1L)
                .firstName("Monika")
                .lastName("Worek")
                .roles(Set.of(musicianRole, inspectorRole))
                .build();

        // when
        UserBasicDTO dto = UserBasicDTOMapper.mapToBasicDto(user);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(user.getId());
        assertThat(dto.getFirstName()).isEqualTo(user.getFirstName());
        assertThat(dto.getLastName()).isEqualTo(user.getLastName());
        assertThat(dto.getRoles()).containsExactlyInAnyOrder("MUSICIAN", "INSPECTOR");
    }
}