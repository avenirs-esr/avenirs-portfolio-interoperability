package fr.avenirsesr.portfolio.interoperability.externalskill.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillReferentialType;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkill;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.output.repository.ExternalSkillRepository;
import fr.avenirsesr.portfolio.interoperability.externalskill.infrastructure.adapter.mapper.ExternalSkillMapper;
import fr.avenirsesr.portfolio.interoperability.externalskill.infrastructure.adapter.model.ExternalSkillEntity;
import fr.avenirsesr.portfolio.interoperability.externalskill.infrastructure.adapter.specification.ExternalSkillSpecification;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ExternalSkillDatabaseRepository
    extends GenericJpaRepositoryAdapter<ExternalSkill, ExternalSkillEntity>
    implements ExternalSkillRepository {
  private final ExternalSkillJpaRepository jpaRepository;

  public ExternalSkillDatabaseRepository(ExternalSkillJpaRepository jpaRepository) {
    super(jpaRepository, jpaRepository, ExternalSkillEntity.class, ExternalSkillMapper.INSTANCE);
    this.jpaRepository = jpaRepository;
  }

  @Override
  public List<ExternalSkill> findAllByExternalId(List<String> skillCodes) {
    return jpaRepository.findAll(ExternalSkillSpecification.hasExternalId(skillCodes)).stream()
        .map(ExternalSkillMapper.INSTANCE::toDomain)
        .toList();
  }

  @Override
  public int countAll(ESkillReferentialType type) {
    return jpaRepository.findAll(ExternalSkillSpecification.hasType(type)).size();
  }

  @Override
  public List<ExternalSkill> findAll() {
    return jpaRepository.findAll().stream().map(ExternalSkillMapper.INSTANCE::toDomain).toList();
  }

  @Override
  public List<ExternalSkill> findAllForIndexing() {
    return jpaRepository.findAllWithCategories().stream()
        .map(ExternalSkillMapper.INSTANCE::toDomain)
        .toList();
  }

  @Override
  public List<ExternalSkill> findAllWithCategoriesByType(ESkillReferentialType type) {
    return jpaRepository.findAllWithCategoriesByType(type).stream()
        .map(ExternalSkillMapper.INSTANCE::toDomain)
        .toList();
  }

  // Used for seeding in api
  @Override
  public List<ExternalSkill> findRandom(int limit) {
    return jpaRepository.findRandom(limit).stream()
        .map(ExternalSkillMapper.INSTANCE::toDomain)
        .toList();
  }
}
