package fr.avenirsesr.portfolio.interoperability.externalskill.infrastructure.fixture;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkill;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkillPagedResult;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.output.OpenSearchIndex;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class OpenSearchIndexStub implements OpenSearchIndex {

  @Override
  public void cleanAndCreateExternalSkillIndex() {
    // No-op (désactivé pendant les tests)
  }

  @Override
  public void indexAll(List<ExternalSkill> externalSkillList) {
    // No-op
  }

  @Override
  public ExternalSkillPagedResult search(String keyword, PageCriteria pageCriteria) {
    return new ExternalSkillPagedResult(
        List.of(), new PageInfo(pageCriteria.page(), pageCriteria.pageSize(), 0));
  }
}
