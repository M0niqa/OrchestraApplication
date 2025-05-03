package com.monika.worek.orchestra.repository;

import com.monika.worek.orchestra.model.AgreementTemplate;
import com.monika.worek.orchestra.model.TemplateType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgreementTemplateRepository extends CrudRepository<AgreementTemplate, Long> {
    List<AgreementTemplate> findByTemplateType(TemplateType templateType);
}
