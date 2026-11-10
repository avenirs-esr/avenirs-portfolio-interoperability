package fr.avenirsesr.portfolio.interoperability.externalskill.application.adapter.controller;

import fr.avenirsesr.portfolio.common.skill.application.adapter.dto.SkillCatalogDTO;
import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillReferentialType;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.input.SkillCatalogService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("interoperability/skill-catalog")
public class SkillCatalogController {
  private static final String RETRY_AFTER_SECONDS = "30";

  private final SkillCatalogService skillCatalogService;

  @PreAuthorize("hasAuthority('external-skill:read')")
  @GetMapping("/{type}")
  public ResponseEntity<SkillCatalogDTO> getCatalog(@PathVariable ESkillReferentialType type) {
    log.debug("Getting the {} skill catalog", type);
    SkillCatalogDTO catalog = skillCatalogService.getCatalog(type);

    if (catalog.nodes().isEmpty()) {
      log.warn("The {} skill catalog is empty (import not done yet?), answering 503", type);
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
          .header(HttpHeaders.RETRY_AFTER, RETRY_AFTER_SECONDS)
          .build();
    }

    return ResponseEntity.ok(catalog);
  }
}
