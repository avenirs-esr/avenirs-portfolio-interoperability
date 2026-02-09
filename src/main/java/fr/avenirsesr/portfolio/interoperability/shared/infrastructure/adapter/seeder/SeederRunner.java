package fr.avenirsesr.portfolio.interoperability.shared.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.common.seeder.infrastructure.configuration.SeedingState;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.output.repository.ExternalSkillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeederRunner implements CommandLineRunner {
  private final ExternalSkillRepository externalSkillRepository;
  private final SeederOrchestrator seederOrchestrator;
  private final SeedingState seedingState;

  @Value("${seeder.enabled:false}")
  private boolean seedEnabled;

  @Override
  public void run(String... args) {
    int externalSkillCount = externalSkillRepository.countAll(null);
    if (!seedEnabled) {
      log.info("Seeder disabled: skipped");
      seedingState.markCompleted();
      return;
    }

    if (externalSkillCount > 0) {
      log.info("{} external skills found. Seeder skipped.", externalSkillCount);
      seedingState.markCompleted();
      return;
    }

    seederOrchestrator.seedAll();
  }
}
