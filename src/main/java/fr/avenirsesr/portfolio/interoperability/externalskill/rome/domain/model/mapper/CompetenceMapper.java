package fr.avenirsesr.portfolio.interoperability.externalskill.rome.domain.model.mapper;

import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillKind;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkillCategory;
import fr.avenirsesr.portfolio.interoperability.externalskill.rome.domain.model.Competence;
import java.util.List;

public class CompetenceMapper {
  public static ExternalSkillCategory toCategoryDomain(
      Competence entity, List<ExternalSkillCategory> categories) {
    ExternalSkillCategory domain = fetchCategory(entity, ESkillKind.DOMAIN, null, categories);
    ExternalSkillCategory issue = fetchCategory(entity, ESkillKind.ISSUE, domain, categories);
    ExternalSkillCategory target = fetchCategory(entity, ESkillKind.TARGET, issue, categories);

    return fetchCategory(entity, ESkillKind.MACRO_SKILL, target, categories);
  }

  private static ExternalSkillCategory fetchCategory(
      Competence entity,
      ESkillKind type,
      ExternalSkillCategory parent,
      List<ExternalSkillCategory> categories) {

    var category = ExternalSkillCategory.of(labelOf(entity, type), parent, type);
    var optionalCategory =
        categories.stream().filter(c -> c.uniqHash() == category.uniqHash()).findFirst();

    if (optionalCategory.isPresent()) {
      return optionalCategory.get();
    } else {
      categories.add(category);
      return category;
    }
  }

  private static String labelOf(Competence entity, ESkillKind type) {
    return switch (type) {
      case DOMAIN ->
          entity.getMacroCompetence().getObjectif().getEnjeu().getDomaineCompetence().getLibelle();
      case ISSUE -> entity.getMacroCompetence().getObjectif().getEnjeu().getLibelle();
      case TARGET -> entity.getMacroCompetence().getObjectif().getLibelle();
      case MACRO_SKILL -> entity.getMacroCompetence().getLibelle();
    };
  }
}
