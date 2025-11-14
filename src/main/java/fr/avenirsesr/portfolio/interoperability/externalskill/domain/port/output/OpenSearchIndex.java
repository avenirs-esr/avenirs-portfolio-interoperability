package fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.output;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkill;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkillPagedResult;
import java.util.List;

public interface OpenSearchIndex {

  void cleanAndCreateExternalSkillIndex();

  void indexAll(List<ExternalSkill> externalSkillList);

  ExternalSkillPagedResult search(String keyword, PageCriteria pageCriteria);
}
