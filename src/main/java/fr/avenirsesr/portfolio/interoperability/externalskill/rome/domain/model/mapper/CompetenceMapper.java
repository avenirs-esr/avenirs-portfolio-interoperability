package fr.avenirsesr.portfolio.interoperability.externalskill.rome.domain.model.mapper;

import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillCategoryType;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkillCategory;
import fr.avenirsesr.portfolio.interoperability.externalskill.rome.domain.model.Competence;
import java.util.List;

public class CompetenceMapper {
  public static ExternalSkillCategory toCategoryDomain(
      Competence entity, List<ExternalSkillCategory> categories) {
    ExternalSkillCategory domain =
        fetchCategory(entity, EExternalSkillCategoryType.DOMAIN, null, categories);
    ExternalSkillCategory issue =
        fetchCategory(entity, EExternalSkillCategoryType.ISSUE, domain, categories);
    ExternalSkillCategory target =
        fetchCategory(entity, EExternalSkillCategoryType.TARGET, issue, categories);

    return fetchCategory(entity, EExternalSkillCategoryType.MACRO_SKILL, target, categories);
  }

  private static ExternalSkillCategory fetchCategory(
      Competence entity,
      EExternalSkillCategoryType type,
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

  private static String labelOf(Competence entity, EExternalSkillCategoryType type) {
    return switch (type) {
      case DOMAIN ->
          entity.getMacroCompetence().getObjectif().getEnjeu().getDomaineCompetence().getLibelle();
      case ISSUE -> entity.getMacroCompetence().getObjectif().getEnjeu().getLibelle();
      case TARGET -> entity.getMacroCompetence().getObjectif().getLibelle();
      case MACRO_SKILL -> entity.getMacroCompetence().getLibelle();
    };
  }
}
