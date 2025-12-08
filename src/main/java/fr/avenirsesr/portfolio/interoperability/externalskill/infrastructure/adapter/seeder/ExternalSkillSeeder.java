package fr.avenirsesr.portfolio.interoperability.externalskill.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.interoperability.externalskill.casoc.domain.port.input.CasocService;
import fr.avenirsesr.portfolio.interoperability.externalskill.casol.domain.port.input.CasolService;
import fr.avenirsesr.portfolio.interoperability.externalskill.infrastructure.adapter.mapper.ExternalSkillMapper;
import fr.avenirsesr.portfolio.interoperability.externalskill.infrastructure.adapter.model.ExternalSkillEntity;
import fr.avenirsesr.portfolio.interoperability.externalskill.xxi.domain.port.input.XXIService;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalSkillSeeder {
  private final XXIService xxiService;
  private final CasocService casocService;
  private final CasolService casolService;

  @Transactional
  public List<ExternalSkillEntity> seed() {
    log.info("Seeding external skills...");

    var xxi = xxiService.syncSkills();
    var casoc = casocService.syncSkills();
    var casol = casolService.syncSkills();

    var externalSkills =
        Stream.of(xxi, casoc, casol)
            .flatMap(Collection::stream)
            .map(ExternalSkillMapper::fromDomain)
            .collect(Collectors.toList());

    log.info("✔ {} external skills synced", externalSkills.size());

    return externalSkills;
  }
}
