package fr.avenirsesr.portfolio.interoperability.externalskill.rome.domain.service;

import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillCategoryType;
import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkill;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkillCategory;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.output.OpenSearchIndex;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.output.repository.ExternalSkillRepository;
import fr.avenirsesr.portfolio.interoperability.externalskill.rome.domain.model.Competence;
import fr.avenirsesr.portfolio.interoperability.externalskill.rome.domain.model.Rome4Version;
import fr.avenirsesr.portfolio.interoperability.externalskill.rome.domain.port.input.RomeExternalSkillService;
import fr.avenirsesr.portfolio.interoperability.externalskill.rome.domain.port.output.RomeExternalSkillApi;
import fr.avenirsesr.portfolio.interoperability.externalskill.rome.domain.port.output.repository.Rome4VersionRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class RomeExternalSkillServiceImpl implements RomeExternalSkillService {
  private final ExternalSkillRepository externalSkillRepository;
  private final Rome4VersionRepository rome4VersionRepository;
  private final RomeExternalSkillApi romeExternalSkillApi;
  private final OpenSearchIndex openSearchIndex;

  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(RomeExternalSkillServiceImpl.class, SharedDataGenerator.class);

  @Override
  public void cleanAndCreateExternalSkillIndex() {
    openSearchIndex.cleanAndCreateExternalSkillIndex();
    List<ExternalSkill> allSkills = externalSkillRepository.findAll();
    openSearchIndex.indexAll(allSkills);
    log.info("Indexed {} external skills in OpenSearch.", allSkills.size());
  }

  @Override
  public List<ExternalSkill> synchronizeExternalSkills(List<ExternalSkill> externalSkillList) {
    List<String> skillCodes =
        externalSkillList.stream()
            .map(ExternalSkill::getExternalId)
            .filter(Objects::nonNull)
            .distinct()
            .toList();

    List<ExternalSkill> existingSkillList = externalSkillRepository.findAllByExternalId(skillCodes);

    Map<String, ExternalSkill> existingSkillByCode =
        existingSkillList.stream()
            .collect(Collectors.toMap(ExternalSkill::getExternalId, Function.identity()));

    List<ExternalSkill> toSave = getExternalSkillsToSave(externalSkillList, existingSkillByCode);
    List<ExternalSkill> savedExternalSkill = externalSkillRepository.saveAll(toSave);
    openSearchIndex.indexAll(savedExternalSkill);
    return savedExternalSkill;
  }

  @Override
  public List<ExternalSkill> syncSkills() {
    log.info("Synchronizing ROME4 external skills...");

    List<Competence> competences = romeExternalSkillApi.fetchAdditionalSkills();

    List<ExternalSkill> externalSkills = competences.stream().map(this::toExternalSkill).toList();

    List<ExternalSkill> savedExternalSkills = synchronizeExternalSkills(externalSkills);

    log.info("{} ROME4 external skills saved and indexed", savedExternalSkills.size());

    return savedExternalSkills;
  }

  @Override
  public boolean checkRomeVersionUpdated() {
    try {
      Rome4Version newVersion = romeExternalSkillApi.fetchRomeVersion();

      boolean shouldSave =
          rome4VersionRepository
              .findFirstByOrderByVersionDesc()
              .map(oldVersion -> newVersion.getVersion() > oldVersion.getVersion())
              .orElse(true);

      if (shouldSave) {
        var rome4Version =
            Rome4Version.create(newVersion.getVersion(), newVersion.getLastModifiedDate());
        rome4VersionRepository.save(rome4Version);
      }

      return shouldSave;
    } catch (Exception e) {
      log.error("An error occurred while fetching ROME 4.0 version.", e);
      return false;
    }
  }

  private List<ExternalSkill> getExternalSkillsToSave(
      List<ExternalSkill> externalSkillList, Map<String, ExternalSkill> existingSkillByCode) {
    List<ExternalSkill> toSave = new ArrayList<>(externalSkillList.size());

    for (ExternalSkill externalSkill : externalSkillList) {
      ExternalSkill existingSkill = existingSkillByCode.get(externalSkill.getExternalId());

      if (existingSkill != null) {
        existingSkill.setExternalSkillCategory(
            externalSkill.getExternalSkillCategory().orElse(null));
        existingSkill.setType(externalSkill.getType());
        toSave.add(existingSkill);
      } else {
        toSave.add(externalSkill);
      }
    }
    return toSave;
  }

  private ExternalSkill toExternalSkill(Competence competence) {
    return ExternalSkill.create(
        dataGenerator.with("rome4ExternalSkillId").uuid(),
        competence.getLibelle(),
        competence.getCode(),
        buildCategory(competence),
        EExternalSkillType.ROME4);
  }

  private ExternalSkillCategory buildCategory(Competence competence) {
    var macroCompetence = competence.getMacroCompetence();
    var objectif = macroCompetence.getObjectif();
    var enjeu = objectif.getEnjeu();
    var domaineCompetence = enjeu.getDomaineCompetence();

    ExternalSkillCategory domain =
        ExternalSkillCategory.of(
            domaineCompetence.getLibelle(), null, EExternalSkillCategoryType.DOMAIN);

    ExternalSkillCategory issue =
        ExternalSkillCategory.of(enjeu.getLibelle(), domain, EExternalSkillCategoryType.ISSUE);

    ExternalSkillCategory target =
        ExternalSkillCategory.of(objectif.getLibelle(), issue, EExternalSkillCategoryType.TARGET);

    return ExternalSkillCategory.of(
        macroCompetence.getLibelle(), target, EExternalSkillCategoryType.MACRO_SKILL);
  }
}
