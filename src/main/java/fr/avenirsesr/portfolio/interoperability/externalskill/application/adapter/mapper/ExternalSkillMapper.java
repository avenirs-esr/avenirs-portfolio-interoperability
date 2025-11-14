package fr.avenirsesr.portfolio.interoperability.externalskill.application.adapter.mapper;

import fr.avenirsesr.portfolio.common.externalskill.application.adapter.dto.ExternalSkillCategoryDTO;
import fr.avenirsesr.portfolio.common.externalskill.application.adapter.dto.ExternalSkillDTO;
import fr.avenirsesr.portfolio.common.externalskill.application.adapter.dto.ExternalSkillDetailsDTO;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkill;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkillCategory;

public interface ExternalSkillMapper {
  static ExternalSkillDTO toExternalSkillDTO(ExternalSkill externalSkill) {
    return new ExternalSkillDTO(
        externalSkill.getId(),
        externalSkill.getLibelle(),
        externalSkill.getCategoryPath().stream().map(ExternalSkillCategory::getLibelle).toList(),
        externalSkill.getType());
  }

  static ExternalSkillDetailsDTO toExternalSkillDetailsDTO(ExternalSkill externalSkill) {
    return new ExternalSkillDetailsDTO(
        externalSkill.getId(),
        externalSkill.getLibelle(),
        externalSkill.getCategoryPath().stream()
            .map(ExternalSkillMapper::toExternalSkillCategoryDTO)
            .toList(),
        externalSkill.getType());
  }

  static ExternalSkillCategoryDTO toExternalSkillCategoryDTO(ExternalSkillCategory category) {
    return new ExternalSkillCategoryDTO(category.getLibelle(), category.getType());
  }
}
