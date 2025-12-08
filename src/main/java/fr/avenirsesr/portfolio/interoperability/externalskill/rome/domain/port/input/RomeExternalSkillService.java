package fr.avenirsesr.portfolio.interoperability.externalskill.rome.domain.port.input;

import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkill;
import java.util.List;

public interface RomeExternalSkillService {

  void cleanAndCreateExternalSkillIndex();

  List<ExternalSkill> synchronizeExternalSkills(List<ExternalSkill> externalSkill);

  boolean checkRomeVersionUpdated();
}
