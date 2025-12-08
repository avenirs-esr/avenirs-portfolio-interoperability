package fr.avenirsesr.portfolio.interoperability.externalskill.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillCategoryType;
import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkill;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkillCategory;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.output.OpenSearchIndex;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.output.repository.ExternalSkillRepository;
import fr.avenirsesr.portfolio.interoperability.externalskill.rome.domain.model.Rome4Version;
import fr.avenirsesr.portfolio.interoperability.externalskill.rome.domain.port.output.RomeExternalSkillApi;
import fr.avenirsesr.portfolio.interoperability.externalskill.rome.domain.port.output.repository.Rome4VersionRepository;
import fr.avenirsesr.portfolio.interoperability.externalskill.rome.domain.service.RomeExternalSkillServiceImpl;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RomeExternalSkillServiceImplTest {

  @Mock private ExternalSkillRepository externalSkillRepository;

  @Mock private Rome4VersionRepository rome4VersionRepository;

  @Mock private RomeExternalSkillApi romeExternalSkillApi;

  @Mock private OpenSearchIndex openSearchIndex;

  @InjectMocks private RomeExternalSkillServiceImpl service;

  private ExternalSkill externalSkill1;
  private ExternalSkill externalSkill2;

  @BeforeEach
  void setUp() {
    var domain1 =
        ExternalSkillCategory.of("domainLibelle1", null, EExternalSkillCategoryType.DOMAIN);
    var issue1 =
        ExternalSkillCategory.of("issueLibelle1", domain1, EExternalSkillCategoryType.ISSUE);
    var target1 =
        ExternalSkillCategory.of("targetLibelle1", issue1, EExternalSkillCategoryType.TARGET);
    var macro1 =
        ExternalSkillCategory.of(
            "macroSkillLibelle1", target1, EExternalSkillCategoryType.MACRO_SKILL);

    externalSkill1 =
        ExternalSkill.create("skillLibelle1", "skillCode1", macro1, EExternalSkillType.ROME4);

    var domain2 =
        ExternalSkillCategory.of("domainLibelle2", null, EExternalSkillCategoryType.DOMAIN);
    var issue2 =
        ExternalSkillCategory.of("issueLibelle2", domain2, EExternalSkillCategoryType.ISSUE);
    var targe2 =
        ExternalSkillCategory.of("targetLibelle2", issue2, EExternalSkillCategoryType.TARGET);
    var macro2 =
        ExternalSkillCategory.of(
            "macroSkillLibelle2", targe2, EExternalSkillCategoryType.MACRO_SKILL);

    externalSkill2 =
        ExternalSkill.create("skillLibelle2", "skillCode2", macro2, EExternalSkillType.ROME4);
  }

  @Test
  void shouldDelegateCleanAndCreateExternalSkillIndex() {
    BddLogger.given("the method cleanAndCreateExternalSkillIndex");

    BddLogger.when("calling the method from the RomeExternalSkillServiceImpl service");
    service.cleanAndCreateExternalSkillIndex();

    BddLogger.then("it should delegate the method to openSearchIndex");
    verify(openSearchIndex).cleanAndCreateExternalSkillIndex();
  }

  @Test
  void shouldSaveAndIndexExternalSkills_WhenNewSkills() {
    BddLogger.given("the method synchronizeExternalSkills");
    List<ExternalSkill> inputSkills = List.of(externalSkill1, externalSkill2);
    when(externalSkillRepository.findAllByExternalId(anyList())).thenReturn(List.of());
    when(externalSkillRepository.saveAll(anyList()))
        .thenAnswer(invocation -> invocation.getArgument(0));

    BddLogger.when(
        "calling the method from the RomeExternalSkillServiceImpl service with new skills");
    List<ExternalSkill> result = service.synchronizeExternalSkills(inputSkills);

    BddLogger.then("it should save and index external skills");
    assertThat(result).hasSize(2);
    verify(externalSkillRepository).findAllByExternalId(List.of("skillCode1", "skillCode2"));
    verify(externalSkillRepository).saveAll(anyList());
    verify(openSearchIndex).indexAll(result);
  }

  @Test
  void shouldUpdateExistingSkillAndIndex() {
    BddLogger.given("the method synchronizeExternalSkills");
    var domain1 =
        ExternalSkillCategory.of("domainLibelle1", null, EExternalSkillCategoryType.DOMAIN);
    var issue1 =
        ExternalSkillCategory.of("issueLibelle1", domain1, EExternalSkillCategoryType.ISSUE);
    var target1 =
        ExternalSkillCategory.of("targetLibelle1", issue1, EExternalSkillCategoryType.TARGET);
    var macro1 =
        ExternalSkillCategory.of(
            "macroSkillLibelle1", target1, EExternalSkillCategoryType.MACRO_SKILL);
    ExternalSkill existingSkill =
        ExternalSkill.create("skillLibelle1", "skillCode1", macro1, EExternalSkillType.ROME4);

    when(externalSkillRepository.findAllByExternalId(anyList())).thenReturn(List.of(existingSkill));
    when(externalSkillRepository.saveAll(anyList()))
        .thenAnswer(invocation -> invocation.getArgument(0));

    BddLogger.when(
        "calling the method from the RomeExternalSkillServiceImpl service with existing skills");
    List<ExternalSkill> result = service.synchronizeExternalSkills(List.of(externalSkill1));

    BddLogger.then("it should update existing external skill and index");
    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getType()).isEqualTo(EExternalSkillType.ROME4);
    verify(openSearchIndex).indexAll(result);
  }

  @Test
  void shouldSaveNewVersion_WhenNoExistingVersion() {
    BddLogger.given("the method checkRomeVersionUpdated");
    Rome4Version newVersion = Rome4Version.create(1, Instant.now());
    when(romeExternalSkillApi.fetchRomeVersion()).thenReturn(newVersion);
    when(rome4VersionRepository.findFirstByOrderByVersionDesc()).thenReturn(Optional.empty());

    BddLogger.when(
        "calling the method from the RomeExternalSkillServiceImpl service with no existing"
            + " version");
    boolean result = service.checkRomeVersionUpdated();

    BddLogger.then("it should save the new version");
    assertTrue(result);
    verify(rome4VersionRepository).save(any(Rome4Version.class));
  }

  @Test
  void shouldSaveNewVersion_WhenNewerVersionFound() {
    BddLogger.given("the method checkRomeVersionUpdated");
    Rome4Version oldVersion = Rome4Version.create(1, Instant.now());
    Rome4Version newVersion = Rome4Version.create(2, Instant.now());

    when(romeExternalSkillApi.fetchRomeVersion()).thenReturn(newVersion);
    when(rome4VersionRepository.findFirstByOrderByVersionDesc())
        .thenReturn(Optional.of(oldVersion));

    BddLogger.when(
        "calling the method from the RomeExternalSkillServiceImpl service with newer version");
    boolean result = service.checkRomeVersionUpdated();

    BddLogger.then("it should save the new version");
    assertTrue(result);
    verify(rome4VersionRepository).save(any(Rome4Version.class));
  }

  @Test
  void shouldNotSave_WhenVersionIsUpToDate() {
    BddLogger.given("the method checkRomeVersionUpdated");
    Rome4Version oldVersion = Rome4Version.create(2, Instant.now());
    Rome4Version newVersion = Rome4Version.create(2, Instant.now());

    when(romeExternalSkillApi.fetchRomeVersion()).thenReturn(newVersion);
    when(rome4VersionRepository.findFirstByOrderByVersionDesc())
        .thenReturn(Optional.of(oldVersion));

    BddLogger.when(
        "calling the method from the RomeExternalSkillServiceImpl service with up-to-date"
            + " version");
    boolean result = service.checkRomeVersionUpdated();

    BddLogger.then("it should not save the new version");
    assertFalse(result);
    verify(rome4VersionRepository, never()).save(any());
  }
}
