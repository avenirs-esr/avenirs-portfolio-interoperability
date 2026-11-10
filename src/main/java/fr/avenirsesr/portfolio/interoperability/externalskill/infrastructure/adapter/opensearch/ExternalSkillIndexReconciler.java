package fr.avenirsesr.portfolio.interoperability.externalskill.infrastructure.adapter.opensearch;

import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillReferentialType;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.output.OpenSearchIndex;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.output.repository.ExternalSkillRepository;
import fr.avenirsesr.portfolio.interoperability.externalskill.rome.domain.port.input.RomeExternalSkillService;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class ExternalSkillIndexReconciler {
  private final ExternalSkillRepository externalSkillRepository;
  private final OpenSearchIndex openSearchIndex;
  private final RomeExternalSkillService romeExternalSkillService;

  private boolean reconciled;

  @Scheduled(fixedDelayString = "${external-skill.index.reconcile.delay}")
  public void reconcile() {
    if (reconciled) {
      return;
    }

    try {
      long expected =
          Arrays.stream(ESkillReferentialType.values())
              .mapToLong(externalSkillRepository::countAll)
              .sum();
      if (expected == 0) {
        log.info("No external skill in database yet, index reconciliation postponed");
        return;
      }

      if (openSearchIndex.countIndexed() != expected) {
        log.warn("External skill index out of sync, rebuilding it from the database");
        romeExternalSkillService.cleanAndCreateExternalSkillIndex();
      }
      reconciled = true;
    } catch (Exception e) {
      log.warn("External skill index not reconciled, it will be retried: {}", e.getMessage());
    }
  }
}
