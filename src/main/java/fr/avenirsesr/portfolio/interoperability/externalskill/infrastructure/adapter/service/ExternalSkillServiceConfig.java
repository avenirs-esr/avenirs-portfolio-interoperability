package fr.avenirsesr.portfolio.interoperability.externalskill.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.input.ExternalSkillService;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.output.OpenSearchIndex;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.output.repository.ExternalSkillRepository;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.service.ExternalSkillServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ExternalSkillServiceConfig {
  private final ExternalSkillRepository externalSkillRepository;
  private final OpenSearchIndex openSearchIndex;

  @Bean
  public ExternalSkillService externalSkillService() {
    return new ExternalSkillServiceImpl(externalSkillRepository, openSearchIndex);
  }
}
