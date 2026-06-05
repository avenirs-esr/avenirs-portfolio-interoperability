package fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.output;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkill;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkillPagedResult;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OpenSearchIndex {

  void cleanAndCreateExternalSkillIndex();

  void indexAll(List<ExternalSkill> externalSkillList);

  ExternalSkillPagedResult search(String keyword, PageCriteria pageCriteria);

  Optional<ExternalSkill> findById(UUID id);
}
