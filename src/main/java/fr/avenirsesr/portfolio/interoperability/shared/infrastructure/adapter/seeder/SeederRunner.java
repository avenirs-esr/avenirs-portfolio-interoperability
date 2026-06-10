package fr.avenirsesr.portfolio.interoperability.shared.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.common.seeder.infrastructure.configuration.SeedingState;
import fr.avenirsesr.portfolio.interoperability.externalskill.rome.domain.port.input.RomeExternalSkillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeederRunner implements CommandLineRunner {
  private final SeederOrchestrator seederOrchestrator;
  private final SeedingState seedingState;
  private final RomeExternalSkillService romeExternalSkillService;

  @Value("${seeder.enabled:false}")
  private boolean seedEnabled;

  @Value("${opensearch.reindex.enabled:false}")
  private boolean reindexEnabled;

  @Override
  public void run(String... args) {
    if (!seedEnabled) {
      log.info("Seeder disabled: skipped");
      seedingState.markCompleted();
      return;
    }

    if (reindexEnabled) {
      log.info("Reindexing starting ....");
      romeExternalSkillService.cleanAndCreateExternalSkillIndex();
      log.info("Reindexing completed.");
    } else {
      log.info("Reindexing disabled: skipped");
    }

    seederOrchestrator.resetAndSeed();
  }
}
