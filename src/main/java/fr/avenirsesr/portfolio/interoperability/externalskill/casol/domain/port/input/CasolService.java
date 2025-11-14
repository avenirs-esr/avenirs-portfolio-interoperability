package fr.avenirsesr.portfolio.interoperability.externalskill.casol.domain.port.input;

import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkill;
import java.util.List;

public interface CasolService {
  List<ExternalSkill> syncSkills();
}
