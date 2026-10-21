package fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.input;

import fr.avenirsesr.portfolio.common.skill.application.adapter.dto.SkillCatalogDTO;
import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillReferentialType;

public interface SkillCatalogService {
  SkillCatalogDTO getCatalog(ESkillReferentialType type);
}
