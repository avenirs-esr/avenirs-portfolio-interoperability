package fr.avenirsesr.portfolio.interoperability.externaluser.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.interoperability.externaluser.domain.port.input.ExternalUserService;
import fr.avenirsesr.portfolio.interoperability.externaluser.domain.port.output.repository.ExternalUserRepository;
import fr.avenirsesr.portfolio.interoperability.externaluser.domain.service.ExternalUserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ExternalUserServiceConfig {
  private final ExternalUserRepository externalUserRepository;

  @Bean
  public ExternalUserService externalUserService() {
    return new ExternalUserServiceImpl(externalUserRepository);
  }
}
