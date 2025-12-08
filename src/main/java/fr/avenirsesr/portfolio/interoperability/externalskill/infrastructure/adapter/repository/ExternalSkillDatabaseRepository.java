package fr.avenirsesr.portfolio.interoperability.externalskill.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
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
    super(
        jpaRepository,
        jpaRepository,
        ExternalSkillMapper::fromDomain,
        ExternalSkillMapper::toDomain);
    this.jpaRepository = jpaRepository;
  }

  @Override
  public List<ExternalSkill> findAllByExternalId(List<String> skillCodes) {
    return jpaRepository.findAll(ExternalSkillSpecification.hasExternalId(skillCodes)).stream()
        .map(ExternalSkillMapper::toDomain)
        .toList();
  }

  @Override
  public int countAll(EExternalSkillType type) {
    return jpaRepository.findAll(ExternalSkillSpecification.hasType(type)).size();
  }

  @Override
  public List<ExternalSkill> findAll() {
    return jpaRepository.findAll().stream().map(ExternalSkillMapper::toDomain).toList();
  }

  // Used for seeding in api
  @Override
  public List<ExternalSkill> findRandom(int limit) {
    return jpaRepository.findRandom(limit).stream().map(ExternalSkillMapper::toDomain).toList();
  }
}
