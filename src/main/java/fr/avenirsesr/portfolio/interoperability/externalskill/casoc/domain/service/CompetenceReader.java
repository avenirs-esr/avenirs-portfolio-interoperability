package fr.avenirsesr.portfolio.interoperability.externalskill.casoc.domain.service;

import fr.avenirsesr.portfolio.interoperability.externalskill.casoc.domain.model.Competence;
import java.util.List;

public interface CompetenceReader {
  List<Competence> readCompetences();
}
