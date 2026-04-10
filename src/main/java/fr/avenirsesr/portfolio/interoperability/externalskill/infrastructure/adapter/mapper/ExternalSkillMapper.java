package fr.avenirsesr.portfolio.interoperability.externalskill.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkill;
import fr.avenirsesr.portfolio.interoperability.externalskill.infrastructure.adapter.model.ExternalSkillEntity;

public class ExternalSkillMapper implements Mapper<ExternalSkillEntity, ExternalSkill> {
  public static final ExternalSkillMapper INSTANCE = new ExternalSkillMapper();

  @Override
  public ExternalSkill toDomain(ExternalSkillEntity entity) {
    return ExternalSkill.toDomain(
        entity.getId(),
        entity.getLibelle(),
        entity.getExternalSkillCategory().map(ExternalSkillCategoryMapper::toDomain).orElse(null),
        entity.getType(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  @Override
  public ExternalSkillEntity fromDomain(ExternalSkill domain) {
    return ExternalSkillEntity.of(
        domain.getId(),
        domain.getLibelle(),
        domain.getType(),
        domain
            .getExternalSkillCategory()
            .map(ExternalSkillCategoryMapper::fromDomain)
            .orElse(null));
  }
}
