package fr.avenirsesr.portfolio.interoperability.externalskill.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkillCategory;
import fr.avenirsesr.portfolio.interoperability.externalskill.infrastructure.adapter.model.ExternalSkillCategoryEntity;

public interface ExternalSkillCategoryMapper {
  static ExternalSkillCategoryEntity fromDomain(ExternalSkillCategory domain) {
    return ExternalSkillCategoryEntity.of(
        domain.getId(),
        domain.getLibelle(),
        domain.getType(),
        domain.getParent().map(ExternalSkillCategoryMapper::fromDomain).orElse(null));
  }

  static ExternalSkillCategory toDomain(ExternalSkillCategoryEntity entity) {
    return ExternalSkillCategory.toDomain(
        entity.getId(),
        entity.getLibelle(),
        entity.getParent().map(ExternalSkillCategoryMapper::toDomain).orElse(null),
        entity.getType(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
