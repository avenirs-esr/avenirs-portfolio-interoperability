package fr.avenirsesr.portfolio.interoperability.shared.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.common.seeder.infrastructure.configuration.SeedingState;
import fr.avenirsesr.portfolio.interoperability.externalskill.infrastructure.adapter.seeder.ExternalSkillSeeder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeederOrchestrator {
  private final ExternalSkillSeeder externalSkillSeeder;
  private final SeedingState seedingState;

  @Transactional()
  public void seedAll() {
    try {
      log.info("Seeding enabled and starting...");
      externalSkillSeeder.seed();
      log.info("✔ Seeding successfully finished");

      seedingState.markCompleted();
    } catch (Exception e) {
      seedingState.markFailed(e);
      log.error("✘ Seeding failed", e);
      throw e;
    }
  }
}
