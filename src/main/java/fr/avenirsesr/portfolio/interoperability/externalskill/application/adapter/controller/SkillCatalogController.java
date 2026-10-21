package fr.avenirsesr.portfolio.interoperability.externalskill.application.adapter.controller;

import fr.avenirsesr.portfolio.common.skill.application.adapter.dto.SkillCatalogDTO;
import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillReferentialType;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.input.SkillCatalogService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
  private final SkillCatalogService skillCatalogService;

  @PreAuthorize("hasAuthority('external-skill:read')")
  @GetMapping("/{type}")
  public ResponseEntity<SkillCatalogDTO> getCatalog(@PathVariable ESkillReferentialType type) {
    log.debug("Getting the {} skill catalog", type);
    return ResponseEntity.ok(skillCatalogService.getCatalog(type));
  }
}
