package fr.avenirsesr.portfolio.interoperability.externalskill.xxi.infrastructure.service;

import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillCategoryType;
import fr.avenirsesr.portfolio.interoperability.externalskill.xxi.domain.model.Category;
import fr.avenirsesr.portfolio.interoperability.externalskill.xxi.domain.model.Competence;
import fr.avenirsesr.portfolio.interoperability.externalskill.xxi.domain.service.CompetenceReader;
import fr.avenirsesr.portfolio.interoperability.shared.infrastructure.utils.FileReader;
import java.util.List;
import java.util.stream.Stream;

public class CsvCompetenceReader implements CompetenceReader {
  private static final String DOMAIN_FILE = "domain.csv";
  private static final String DOMAIN_CSV_SEPARATOR = ",";
  private static final String ISSUE_FILE = "issue.csv";
  private static final String ISSUE_CSV_SEPARATOR = ";";
  private static final String COMPETENCE_FILE = "skill.csv";
  private static final String COMPETENCE_CSV_SEPARATOR = ";";

  @Override
  public List<Competence> readCompetences() {
    var categories = readCategories();

    return FileReader.readCSV(
        "/external-skill/xxi/" + COMPETENCE_FILE,
        COMPETENCE_CSV_SEPARATOR,
        tokens ->
            new Competence(
                Integer.parseInt(tokens[0].trim()),
                tokens[1].trim(),
                categories.stream()
                    .filter(category -> category.type() == EExternalSkillCategoryType.ISSUE)
                    .filter(category -> category.id() == Integer.parseInt(tokens[2].trim()))
                    .findAny()
                    .orElseThrow()));
  }

  private List<Category> readCategories() {
    var domains =
        FileReader.readCSV(
            "/external-skill/xxi/" + DOMAIN_FILE,
            DOMAIN_CSV_SEPARATOR,
            tokens ->
                new Category(
                    Integer.parseInt(tokens[0].trim()),
                    tokens[1].trim(),
                    null,
                    EExternalSkillCategoryType.DOMAIN));

    var issues =
        FileReader.readCSV(
            "/external-skill/xxi/" + ISSUE_FILE,
            ISSUE_CSV_SEPARATOR,
            tokens ->
                new Category(
                    Integer.parseInt(tokens[0].trim()),
                    tokens[1].trim(),
                    domains.stream()
                        .filter(d -> d.id() == Integer.parseInt(tokens[2].trim()))
                        .findAny()
                        .orElseThrow(),
                    EExternalSkillCategoryType.ISSUE));

    return Stream.concat(domains.stream(), issues.stream()).toList();
  }
}
