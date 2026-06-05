package fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.input;

import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkill;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExternalSkillService {
  List<ExternalSkill> getRandomExternalSkills(int count);

  Optional<ExternalSkill> getById(UUID id);
}
