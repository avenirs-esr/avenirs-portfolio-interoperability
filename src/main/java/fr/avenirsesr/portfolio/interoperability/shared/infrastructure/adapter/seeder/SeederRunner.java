package fr.avenirsesr.portfolio.interoperability.shared.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.common.seeder.infrastructure.configuration.SeedingState;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.output.repository.ExternalSkillRepository;
import fr.avenirsesr.portfolio.interoperability.externalskill.infrastructure.adapter.seeder.ExternalSkillSeeder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class SeederRunner implements CommandLineRunner {
  private final ExternalSkillSeeder externalSkillSeeder;
  private final ExternalSkillRepository externalSkillRepository;
  private final SeedingState seedingState;

  @Value("${seeder.enabled:false}")
  private boolean seedEnabled;

  public SeederRunner(
      ExternalSkillSeeder externalSkillSeeder,
      ExternalSkillRepository externalSkillRepository,
      SeedingState seedingState) {
    this.externalSkillSeeder = externalSkillSeeder;
    this.externalSkillRepository = externalSkillRepository;
    this.seedingState = seedingState;
  }

  @Transactional
  @Override
  public void run(String... args) {
    try {
      int externalSkillCount = externalSkillRepository.countAll(null);

      if (seedEnabled && externalSkillCount == 0) {
        log.info("Seeding enabled and starting...");
        externalSkillSeeder.seed();
        log.info("✔ Seeding successfully finished");
      } else {
        log.info(
            "{} external skills found. Seeder enabled: {} => seeding skipped",
            externalSkillCount,
            seedEnabled);
      }

      seedingState.markCompleted();
    } catch (Exception e) {
      seedingState.markFailed(e);
      log.error("✘ Seeding failed", e);
      throw e;
    }
  }
}
