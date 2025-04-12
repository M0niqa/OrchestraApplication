package com.monika.worek.orchestra.repository;

import com.monika.worek.orchestra.model.AgreementTemplate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgreementTemplateRepository extends CrudRepository<AgreementTemplate, Long> {
}
