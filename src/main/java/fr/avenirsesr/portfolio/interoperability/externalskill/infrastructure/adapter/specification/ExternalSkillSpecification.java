package fr.avenirsesr.portfolio.interoperability.externalskill.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import fr.avenirsesr.portfolio.interoperability.externalskill.infrastructure.adapter.model.ExternalSkillEntity;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class ExternalSkillSpecification {
  public static Specification<ExternalSkillEntity> hasExternalId(List<String> externalIds) {
    return (root, query, criteriaBuilder) -> {
      if (externalIds == null || externalIds.isEmpty()) {
        return criteriaBuilder.conjunction();
      }
      return root.get("externalId").in(externalIds);
    };
  }

  public static Specification<ExternalSkillEntity> hasType(EExternalSkillType type) {
    return (root, query, builder) -> builder.equal(root.get("type"), type);
  }
}
