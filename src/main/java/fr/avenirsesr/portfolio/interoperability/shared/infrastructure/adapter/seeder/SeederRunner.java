package fr.avenirsesr.portfolio.interoperability.shared.infrastructure.adapter.seeder;

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

  @Value("${seeder.enabled:false}")
  private boolean seedEnabled;

  public SeederRunner(
      ExternalSkillSeeder externalSkillSeeder, ExternalSkillRepository externalSkillRepository) {
    this.externalSkillSeeder = externalSkillSeeder;
    this.externalSkillRepository = externalSkillRepository;
  }

  @Transactional
  @Override
  public void run(String... args) {
    int externalSkillCount = externalSkillRepository.countAll(null);

    if (seedEnabled && externalSkillCount == 0) {
      log.info("Seeding enabled and starting...");

      var savedExternalSkills = externalSkillSeeder.seed();

      log.info("✔ Seeding successfully finished");
    } else {
      log.info("{} external skills found. Seeder is disabled: seeding skipped", externalSkillCount);
    }
  }
}
