package fr.avenirsesr.portfolio.interoperability.externalskill.xxi.domain.service;

import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkill;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkillCategory;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.output.OpenSearchIndex;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.output.repository.ExternalSkillRepository;
import fr.avenirsesr.portfolio.interoperability.externalskill.xxi.domain.model.Category;
import fr.avenirsesr.portfolio.interoperability.externalskill.xxi.domain.port.input.XXIService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class XXIServiceImpl implements XXIService {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(XXIServiceImpl.class, SharedDataGenerator.class);

  private final CompetenceReader competenceReader;
  private final OpenSearchIndex openSearchIndex;
  private final ExternalSkillRepository externalSkillRepository;

  @Override
  public List<ExternalSkill> syncSkills() {
    var competences = competenceReader.readCompetences();
    var categories = new ArrayList<ExternalSkillCategory>();
    var externalSkills =
        competences.stream()
            .map(
                competence ->
                    ExternalSkill.create(
                        dataGenerator.with("externalSkillId").uuid(),
                        competence.libelle(),
                        buildCategory(competence.category(), categories),
                        EExternalSkillType.XXI))
            .toList();

    externalSkillRepository.saveAll(externalSkills);
    openSearchIndex.indexAll(externalSkills);
    log.info("{} External skills from XXI saved and indexed", externalSkills.size());
    return externalSkills;
  }

  private ExternalSkillCategory buildCategory(
      Category category, ArrayList<ExternalSkillCategory> categories) {
    var externalSkillCategory =
        ExternalSkillCategory.of(
            category.libelle(),
            Optional.ofNullable(category.parent())
                .map(c -> buildCategory(c, categories))
                .orElse(null),
            category.type());
    var categoryToSave =
        categories.stream()
            .filter(c -> c.uniqHash() == externalSkillCategory.uniqHash())
            .findAny()
            .orElse(externalSkillCategory);

    if (categoryToSave.equals(externalSkillCategory)) {
      addCategoriesRecursively(externalSkillCategory, categories);
    }

    return categoryToSave;
  }

  private void addCategoriesRecursively(
      ExternalSkillCategory category, ArrayList<ExternalSkillCategory> categories) {
    categories.add(category);
    category.getParent().ifPresent(parent -> addCategoriesRecursively(parent, categories));
  }
}
