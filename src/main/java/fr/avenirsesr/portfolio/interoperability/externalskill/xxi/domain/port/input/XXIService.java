package fr.avenirsesr.portfolio.interoperability.externalskill.xxi.domain.port.input;

import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkill;
import java.util.List;

public interface XXIService {
  List<ExternalSkill> syncSkills();
}
