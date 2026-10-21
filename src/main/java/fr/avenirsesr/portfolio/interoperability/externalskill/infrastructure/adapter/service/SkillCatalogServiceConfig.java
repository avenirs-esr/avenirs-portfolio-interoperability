package fr.avenirsesr.portfolio.interoperability.externalskill.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.input.SkillCatalogService;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.output.repository.ExternalSkillRepository;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.service.SkillCatalogServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SkillCatalogServiceConfig {

  @Bean
  public SkillCatalogService skillCatalogService(ExternalSkillRepository externalSkillRepository) {
    return new SkillCatalogServiceImpl(externalSkillRepository);
  }
}
