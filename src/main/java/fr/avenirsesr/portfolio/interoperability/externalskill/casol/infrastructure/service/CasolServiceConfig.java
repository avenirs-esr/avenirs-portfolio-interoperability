package fr.avenirsesr.portfolio.interoperability.externalskill.casol.infrastructure.service;

import fr.avenirsesr.portfolio.interoperability.externalskill.casol.domain.port.input.CasolService;
import fr.avenirsesr.portfolio.interoperability.externalskill.casol.domain.service.CasolServiceImpl;
import fr.avenirsesr.portfolio.interoperability.externalskill.casol.domain.service.CompetenceReader;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.output.OpenSearchIndex;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.output.repository.ExternalSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CasolServiceConfig {
  private final ExternalSkillRepository externalSkillRepository;
  private final OpenSearchIndex openSearchIndex;
  private final CompetenceReader competenceReader;

  @Bean
  public CasolService casolService() {
    return new CasolServiceImpl(competenceReader, openSearchIndex, externalSkillRepository);
  }
}
