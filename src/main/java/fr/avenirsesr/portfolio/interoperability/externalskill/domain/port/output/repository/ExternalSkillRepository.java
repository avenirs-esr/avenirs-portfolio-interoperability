package fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.output.repository;

import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillReferentialType;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkill;
import java.util.List;

public interface ExternalSkillRepository extends GenericRepositoryPort<ExternalSkill> {
  List<ExternalSkill> findAllByExternalId(List<String> skillCodes);

  int countAll(ESkillReferentialType type);

  List<ExternalSkill> findAll();

  List<ExternalSkill> findAllForIndexing();

  List<ExternalSkill> findAllWithCategoriesByType(ESkillReferentialType type);

  List<ExternalSkill> findRandom(int limit);
}
