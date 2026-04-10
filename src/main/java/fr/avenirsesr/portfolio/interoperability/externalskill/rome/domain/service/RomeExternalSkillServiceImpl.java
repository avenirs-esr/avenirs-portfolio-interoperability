package fr.avenirsesr.portfolio.interoperability.externalskill.rome.domain.service;

import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkill;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.output.OpenSearchIndex;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.output.repository.ExternalSkillRepository;
import fr.avenirsesr.portfolio.interoperability.externalskill.rome.domain.model.Rome4Version;
import fr.avenirsesr.portfolio.interoperability.externalskill.rome.domain.port.input.RomeExternalSkillService;
import fr.avenirsesr.portfolio.interoperability.externalskill.rome.domain.port.output.RomeExternalSkillApi;
import fr.avenirsesr.portfolio.interoperability.externalskill.rome.domain.port.output.repository.Rome4VersionRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
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

  @Override
  public void cleanAndCreateExternalSkillIndex() {
    openSearchIndex.cleanAndCreateExternalSkillIndex();
    List<ExternalSkill> allSkills = externalSkillRepository.findAll();
    openSearchIndex.indexAll(allSkills);
    log.info("Indexed {} external skills in OpenSearch.", allSkills.size());
  }

  @Override
  public List<ExternalSkill> synchronizeExternalSkills(List<ExternalSkill> externalSkillList) {
    List<UUID> skillCodes =
        externalSkillList.stream()
            .map(ExternalSkill::getId)
            .filter(Objects::nonNull)
            .distinct()
            .toList();

    List<ExternalSkill> existingSkillList = externalSkillRepository.findAllById(skillCodes);

    Map<UUID, ExternalSkill> existingSkillByCode =
        existingSkillList.stream()
            .collect(Collectors.toMap(ExternalSkill::getId, Function.identity()));

    List<ExternalSkill> toSave = getExternalSkillsToSave(externalSkillList, existingSkillByCode);
    List<ExternalSkill> savedExternalSkill = externalSkillRepository.saveAll(toSave);
    openSearchIndex.indexAll(savedExternalSkill);
    return savedExternalSkill;
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
      List<ExternalSkill> externalSkillList, Map<UUID, ExternalSkill> existingSkillByCode) {
    List<ExternalSkill> toSave = new ArrayList<>(externalSkillList.size());

    for (ExternalSkill externalSkill : externalSkillList) {
      ExternalSkill existingSkill = existingSkillByCode.get(externalSkill.getId());

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
}
