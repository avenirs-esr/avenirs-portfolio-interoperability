package fr.avenirsesr.portfolio.interoperability.externalskill.casoc.domain.port.input;

import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkill;
import java.util.List;

public interface CasocService {
  List<ExternalSkill> syncSkills();
}
