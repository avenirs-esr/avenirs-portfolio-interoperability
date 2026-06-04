package fr.avenirsesr.portfolio.interoperability.shared.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.common.dependency.domain.port.input.DependencyChecker;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.configuration.SeedingState;
import fr.avenirsesr.portfolio.interoperability.externalskill.infrastructure.adapter.seeder.ExternalSkillSeeder;
import fr.avenirsesr.portfolio.interoperability.externaluser.infrastructure.adapter.seeder.ExternalUserSeeder;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeederOrchestrator {
  private final ExternalSkillSeeder externalSkillSeeder;
  private final ExternalUserSeeder externalUserSeeder;
  private final SeedingState seedingState;

  private final DependencyChecker dependencyChecker;

  private final ReentrantLock lock = new ReentrantLock();
  private final JdbcTemplate jdbcTemplate;

  @Value("${seeder.schema:dev}")
  private String schemaName;

  @Value("${opensearch.health.url}")
  private String openSearchHealthUrl;

  @Transactional()
  public void seedAll() {
    try {
      log.info("Seeding enabled and starting...");
      dependencyChecker.checkAndWait("OpenSearch", openSearchHealthUrl);
      externalSkillSeeder.seed();
      externalUserSeeder.seed();
      seedingState.markCompleted();
      log.info("✔ Seeding successfully finished");
    } catch (Exception e) {
      seedingState.markFailed(e);
      log.error("✘ Seeding failed", e);
      throw e;
    }
  }

  @Transactional()
  public void clearAll() {
    List<String> tables =
        jdbcTemplate.queryForList(
            """
            SELECT tablename
            FROM pg_tables
            WHERE schemaname = ?
              AND tablename NOT IN ('databasechangelog', 'databasechangeloglock')
            """,
            String.class,
            schemaName);

    if (tables.isEmpty()) {
      log.warn("No tables found in schema '{}'", schemaName);
      return;
    }

    String joined =
        tables.stream()
            .map(t -> "\"" + schemaName + "\".\"" + t + "\"")
            .reduce((a, b) -> a + ", " + b)
            .orElseThrow();

    String sql = "TRUNCATE TABLE " + joined + " RESTART IDENTITY CASCADE";

    log.warn("Resetting DB: {}", sql);
    jdbcTemplate.execute(sql);
  }

  public void resetAndSeed() {
    if (!lock.tryLock()) {
      throw new IllegalStateException("Seeding already running");
    }
    try {
      clearAll();
      seedAll();
    } finally {
      lock.unlock();
    }
  }
}
