package fr.avenirsesr.portfolio.interoperability.externalskill.casol.domain.service;

import fr.avenirsesr.portfolio.interoperability.externalskill.casol.domain.model.Competence;
import java.util.List;

public interface CompetenceReader {
  List<Competence> readCompetences();
}
