package com.monika.worek.orchestra.repository;


import com.monika.worek.orchestra.model.Musician;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface MusicianRepository extends CrudRepository<Musician, Long> {

    Optional<Musician> findByEmail(String email);
}
