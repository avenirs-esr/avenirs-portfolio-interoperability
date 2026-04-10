package fr.avenirsesr.portfolio.interoperability.externalskill.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import fr.avenirsesr.portfolio.interoperability.externalskill.infrastructure.adapter.model.ExternalSkillEntity;
import org.springframework.data.jpa.domain.Specification;

public class ExternalSkillSpecification {

  public static Specification<ExternalSkillEntity> hasType(EExternalSkillType type) {
    return (root, query, builder) -> builder.equal(root.get("type"), type);
  }
}
