package fr.avenirsesr.portfolio.interoperability.externalskill.application.adapter.controller;

import fr.avenirsesr.portfolio.common.data.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.externalskill.application.adapter.dto.ExternalSkillDTO;
import fr.avenirsesr.portfolio.common.externalskill.application.adapter.dto.ExternalSkillDetailsDTO;
import fr.avenirsesr.portfolio.interoperability.externalskill.application.adapter.mapper.ExternalSkillMapper;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.input.ExternalSkillService;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.output.OpenSearchIndex;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping({"interoperability/external-skills"})
public class ExternalSkillController {
  private final OpenSearchIndex openSearchIndex;
  private final ExternalSkillService externalSkillService;

  @PreAuthorize("hasAuthority('external-skill:read')")
  @GetMapping(path = "/search")
  public ResponseEntity<PagedResponse<ExternalSkillDTO>> searchExternalSkills(
      @RequestParam String keyword,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize) {
    var result = openSearchIndex.search(keyword, new PageCriteria(page, pageSize));
    return ResponseEntity.ok(
        new PagedResponse<>(
            result.content().stream().map(ExternalSkillMapper::toExternalSkillDTO).toList(),
            PageInfoDTO.fromDomain(result.pageInfo())));
  }

  @PreAuthorize("hasAuthority('external-skill:read')")
  @GetMapping(path = "/random")
  public ResponseEntity<List<ExternalSkillDTO>> getRandomSkills(
      @RequestParam(defaultValue = "200") int count) {
    log.debug("Getting {} random external skills", count);
    return ResponseEntity.ok(
        externalSkillService.getRandomExternalSkills(count).stream()
            .map(ExternalSkillMapper::toExternalSkillDTO)
            .toList());
  }

  @PreAuthorize("hasAuthority('external-skill:read')")
  @GetMapping(path = "/{id}")
  public ResponseEntity<ExternalSkillDetailsDTO> getExternalSkillById(@PathVariable UUID id) {
    log.debug("Getting external skill details for id: {}", id);

    return externalSkillService
        .getById(id)
        .map(ExternalSkillMapper::toExternalSkillDetailsDTO)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }
}
