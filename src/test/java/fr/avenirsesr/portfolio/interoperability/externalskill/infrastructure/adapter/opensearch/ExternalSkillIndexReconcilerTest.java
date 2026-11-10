package fr.avenirsesr.portfolio.interoperability.externalskill.infrastructure.adapter.opensearch;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillReferentialType;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.output.OpenSearchIndex;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.output.repository.ExternalSkillRepository;
import fr.avenirsesr.portfolio.interoperability.externalskill.rome.domain.port.input.RomeExternalSkillService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExternalSkillIndexReconcilerTest {

  @Mock private ExternalSkillRepository externalSkillRepository;
  @Mock private OpenSearchIndex openSearchIndex;
  @Mock private RomeExternalSkillService romeExternalSkillService;

  private ExternalSkillIndexReconciler reconciler;

  @BeforeEach
  void setUp() {
    reconciler =
        new ExternalSkillIndexReconciler(
            externalSkillRepository, openSearchIndex, romeExternalSkillService);
  }

  private void givenSkillsInDatabase(int rome4Count) {
    when(externalSkillRepository.countAll(any())).thenReturn(0);
    when(externalSkillRepository.countAll(ESkillReferentialType.ROME4)).thenReturn(rome4Count);
  }

  @Test
  void shouldWaitForTheImportWhenTheDatabaseIsEmpty() {
    BddLogger.given("no external skill in database yet");
    givenSkillsInDatabase(0);

    BddLogger.when("reconciling");
    reconciler.reconcile();

    BddLogger.then("the index is left untouched");
    verify(romeExternalSkillService, never()).cleanAndCreateExternalSkillIndex();
  }

  @Test
  void shouldRebuildTheIndexWhenItIsOutOfSync() {
    BddLogger.given("skills in database but an empty index");
    givenSkillsInDatabase(10);
    when(openSearchIndex.countIndexed()).thenReturn(0L);

    BddLogger.when("reconciling");
    reconciler.reconcile();

    BddLogger.then("the index is rebuilt from the database");
    verify(romeExternalSkillService).cleanAndCreateExternalSkillIndex();
  }

  @Test
  void shouldLeaveTheIndexUntouchedWhenItIsConsistent() {
    BddLogger.given("an index holding as many documents as the database");
    givenSkillsInDatabase(10);
    when(openSearchIndex.countIndexed()).thenReturn(10L);

    BddLogger.when("reconciling");
    reconciler.reconcile();

    BddLogger.then("nothing is rebuilt");
    verify(romeExternalSkillService, never()).cleanAndCreateExternalSkillIndex();
  }
}
