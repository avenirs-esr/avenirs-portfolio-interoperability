package fr.avenirsesr.portfolio.interoperability.externalskill.rome.domain.port.output;

import fr.avenirsesr.portfolio.interoperability.externalskill.rome.domain.model.Competence;
import fr.avenirsesr.portfolio.interoperability.externalskill.rome.domain.model.Rome4Version;
import java.util.List;

public interface RomeExternalSkillApi {
  Rome4Version fetchRomeVersion();

  List<Competence> fetchAdditionalSkills();
}
