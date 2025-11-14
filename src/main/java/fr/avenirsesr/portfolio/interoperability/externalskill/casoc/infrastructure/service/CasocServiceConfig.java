package fr.avenirsesr.portfolio.interoperability.externalskill.casoc.infrastructure.service;

import fr.avenirsesr.portfolio.interoperability.externalskill.casoc.domain.port.input.CasocService;
import fr.avenirsesr.portfolio.interoperability.externalskill.casoc.domain.service.CasocServiceImpl;
import fr.avenirsesr.portfolio.interoperability.externalskill.casoc.domain.service.CompetenceReader;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.output.OpenSearchIndex;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.output.repository.ExternalSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CasocServiceConfig {
  private final ExternalSkillRepository externalSkillRepository;
  private final OpenSearchIndex openSearchIndex;
  private final CompetenceReader competenceReader;

  @Bean
  public CasocService casocService() {
    return new CasocServiceImpl(competenceReader, openSearchIndex, externalSkillRepository);
  }
}
