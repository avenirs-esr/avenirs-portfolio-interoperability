package fr.avenirsesr.portfolio.interoperability.externalskill.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkill;
import fr.avenirsesr.portfolio.interoperability.externalskill.infrastructure.adapter.model.ExternalSkillEntity;

public interface ExternalSkillMapper {

  static ExternalSkill toDomain(ExternalSkillEntity entity) {
    return ExternalSkill.toDomain(
        entity.getId(),
        entity.getLibelle(),
        entity.getExternalId(),
        entity.getExternalSkillCategory().map(ExternalSkillCategoryMapper::toDomain).orElse(null),
        entity.getType(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  static ExternalSkillEntity fromDomain(ExternalSkill domain) {
    return ExternalSkillEntity.of(
        domain.getId(),
        domain.getExternalId(),
        domain.getLibelle(),
        domain.getType(),
        domain
            .getExternalSkillCategory()
            .map(ExternalSkillCategoryMapper::fromDomain)
            .orElse(null));
  }
}
