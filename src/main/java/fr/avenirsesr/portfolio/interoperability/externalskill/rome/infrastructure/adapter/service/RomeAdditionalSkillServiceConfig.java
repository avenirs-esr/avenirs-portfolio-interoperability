package fr.avenirsesr.portfolio.interoperability.externalskill.rome.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.output.OpenSearchIndex;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.output.repository.ExternalSkillRepository;
import fr.avenirsesr.portfolio.interoperability.externalskill.rome.domain.port.input.RomeExternalSkillService;
import fr.avenirsesr.portfolio.interoperability.externalskill.rome.domain.port.output.RomeExternalSkillApi;
import fr.avenirsesr.portfolio.interoperability.externalskill.rome.domain.port.output.repository.Rome4VersionRepository;
import fr.avenirsesr.portfolio.interoperability.externalskill.rome.domain.service.RomeExternalSkillServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RomeAdditionalSkillServiceConfig {
  private final Rome4VersionRepository rome4VersionRepository;
  private final RomeExternalSkillApi romeAdditionalSkillApi;
  private final ExternalSkillRepository externalSkillRepository;
  private final OpenSearchIndex openSearchIndex;

  @Bean
  public RomeExternalSkillService romeAdditionalSkillService() {
    return new RomeExternalSkillServiceImpl(
        externalSkillRepository, rome4VersionRepository, romeAdditionalSkillApi, openSearchIndex);
  }
}
