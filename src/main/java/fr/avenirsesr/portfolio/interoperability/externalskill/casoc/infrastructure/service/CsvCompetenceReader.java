package fr.avenirsesr.portfolio.interoperability.externalskill.casoc.infrastructure.service;

import fr.avenirsesr.portfolio.common.seeder.infrastructure.utils.FileReader;
import fr.avenirsesr.portfolio.interoperability.externalskill.casoc.domain.model.Category;
import fr.avenirsesr.portfolio.interoperability.externalskill.casoc.domain.model.Competence;
import fr.avenirsesr.portfolio.interoperability.externalskill.casoc.domain.service.CompetenceReader;
import java.util.List;

public class CsvCompetenceReader implements CompetenceReader {
  private static final String DOMAIN_FILE = "domain.csv";
  private static final String COMPETENCE_FILE = "skill.csv";
  private static final String CSV_SEPARATOR = ";";

  @Override
  public List<Competence> readCompetences() {
    var categories = readCategories();

    return FileReader.readCSV(
        "/external-skill/casoc/" + COMPETENCE_FILE,
        CSV_SEPARATOR,
        tokens ->
            new Competence(
                Integer.parseInt(tokens[0].trim()),
                tokens[1].trim(),
                categories.stream()
                    .filter(category -> category.id() == Integer.parseInt(tokens[2].trim()))
                    .findAny()
                    .orElseThrow()));
  }

  private List<Category> readCategories() {
    return FileReader.readCSV(
        "/external-skill/casoc/" + DOMAIN_FILE,
        CSV_SEPARATOR,
        tokens -> new Category(Integer.parseInt(tokens[0].trim()), tokens[1].trim()));
  }
}
