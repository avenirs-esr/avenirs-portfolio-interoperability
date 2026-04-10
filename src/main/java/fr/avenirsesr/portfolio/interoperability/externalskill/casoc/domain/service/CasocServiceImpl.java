package fr.avenirsesr.portfolio.interoperability.externalskill.casoc.domain.service;

import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillCategoryType;
import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.interoperability.externalskill.casoc.domain.model.Competence;
import fr.avenirsesr.portfolio.interoperability.externalskill.casoc.domain.port.input.CasocService;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkill;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkillCategory;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.output.OpenSearchIndex;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.output.repository.ExternalSkillRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CasocServiceImpl implements CasocService {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(CasocServiceImpl.class, SharedDataGenerator.class);

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
                        buildCategory(competence, categories),
                        EExternalSkillType.CASOC))
            .toList();

    externalSkillRepository.saveAll(externalSkills);
    openSearchIndex.indexAll(externalSkills);
    log.info("{} External skills from CASOC saved and indexed", externalSkills.size());
    return externalSkills;
  }

  private ExternalSkillCategory buildCategory(
      Competence competence, ArrayList<ExternalSkillCategory> categories) {
    var externalSkillCategory =
        ExternalSkillCategory.of(
            competence.category().libelle(), null, EExternalSkillCategoryType.DOMAIN);

    var categoryToSave =
        categories.stream()
            .filter(c -> c.uniqHash() == externalSkillCategory.uniqHash())
            .findFirst()
            .orElse(externalSkillCategory);

    if (categoryToSave.equals(externalSkillCategory)) {
      categories.add(externalSkillCategory);
    }
    return categoryToSave;
  }
}
