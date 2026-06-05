package fr.avenirsesr.portfolio.interoperability.externalskill.domain.service;

import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkill;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.input.ExternalSkillService;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.output.OpenSearchIndex;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.output.repository.ExternalSkillRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ExternalSkillServiceImpl implements ExternalSkillService {
  private final ExternalSkillRepository externalSkillRepository;
  private final OpenSearchIndex openSearchIndex;

  @Override
  public List<ExternalSkill> getRandomExternalSkills(int count) {
    return externalSkillRepository.findRandom(count);
  }

  @Override
  public Optional<ExternalSkill> getById(UUID id) {
    return externalSkillRepository.findById(id).or(() -> findInOpenSearchAndSave(id));
  }

  private Optional<ExternalSkill> findInOpenSearchAndSave(UUID id) {
    log.debug("External skill [{}] not found in database. Searching in OpenSearch...", id);

    return openSearchIndex
        .findById(id)
        .map(
            externalSkill -> {
              ExternalSkill savedExternalSkill = externalSkillRepository.save(externalSkill);

              log.info(
                  "External skill [{}] found in OpenSearch and saved in database",
                  savedExternalSkill.getId());

              return savedExternalSkill;
            });
  }
}
