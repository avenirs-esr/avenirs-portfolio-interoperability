package fr.avenirsesr.portfolio.interoperability.externalskill.domain.service;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.common.skill.application.adapter.dto.SkillCatalogDTO;
import fr.avenirsesr.portfolio.common.skill.application.adapter.dto.SkillNodeDTO;
import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillReferentialType;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkill;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkillCategory;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.input.SkillCatalogService;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.output.repository.ExternalSkillRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class SkillCatalogServiceImpl implements SkillCatalogService {
  private final ExternalSkillRepository externalSkillRepository;

  @Override
  public SkillCatalogDTO getCatalog(ESkillReferentialType type) {
    List<ExternalSkill> externalSkills = externalSkillRepository.findAllWithCategoriesByType(type);

    List<SkillNodeDTO> nodes = new ArrayList<>(collectCategoryNodes(externalSkills).values());
    externalSkills.forEach(externalSkill -> nodes.add(toSkillNode(externalSkill)));

    log.debug("Built the {} skill catalog with {} nodes", type, nodes.size());

    return new SkillCatalogDTO(type, type.name(), type.name(), nodes);
  }

  private Map<UUID, SkillNodeDTO> collectCategoryNodes(List<ExternalSkill> externalSkills) {
    Map<UUID, SkillNodeDTO> nodeById = new LinkedHashMap<>();

    externalSkills.forEach(
        externalSkill ->
            externalSkill
                .getCategoryPath()
                .forEach(
                    category ->
                        nodeById.computeIfAbsent(
                            category.getId(), id -> toCategoryNode(category))));

    return nodeById;
  }

  private SkillNodeDTO toCategoryNode(ExternalSkillCategory category) {
    return new SkillNodeDTO(
        category.getId(),
        null,
        category.getLibelle(),
        null,
        category.getParent().map(AvenirsBaseModel::getId).orElse(null),
        category.getType(),
        false);
  }

  private SkillNodeDTO toSkillNode(ExternalSkill externalSkill) {
    return new SkillNodeDTO(
        externalSkill.getId(),
        externalSkill.getExternalId(),
        externalSkill.getLibelle(),
        null,
        externalSkill.getExternalSkillCategory().map(AvenirsBaseModel::getId).orElse(null),
        null,
        true);
  }
}
