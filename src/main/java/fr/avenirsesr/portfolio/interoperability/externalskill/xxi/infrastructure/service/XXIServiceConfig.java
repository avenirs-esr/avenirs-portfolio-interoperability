package fr.avenirsesr.portfolio.interoperability.externalskill.xxi.infrastructure.service;

import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.output.OpenSearchIndex;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.output.repository.ExternalSkillRepository;
import fr.avenirsesr.portfolio.interoperability.externalskill.xxi.domain.port.input.XXIService;
import fr.avenirsesr.portfolio.interoperability.externalskill.xxi.domain.service.CompetenceReader;
import fr.avenirsesr.portfolio.interoperability.externalskill.xxi.domain.service.XXIServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class XXIServiceConfig {
  private final ExternalSkillRepository externalSkillRepository;
  private final OpenSearchIndex openSearchIndex;
  private final CompetenceReader competenceReader;

  @Bean
  public XXIService xxiServiceConfig() {
    return new XXIServiceImpl(competenceReader, openSearchIndex, externalSkillRepository);
  }
}
