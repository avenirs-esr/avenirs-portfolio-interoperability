package fr.avenirsesr.portfolio.interoperability.externalskill.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.common.skill.application.adapter.dto.SkillNodeDTO;
import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillKind;
import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillReferentialType;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkill;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkillCategory;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.output.repository.ExternalSkillRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SkillCatalogServiceImplTest {

  @Mock private ExternalSkillRepository externalSkillRepository;

  @InjectMocks private SkillCatalogServiceImpl service;

  @Test
  void shouldFlattenCategoriesAndSkillsIntoNodes() {
    BddLogger.given("two skills sharing a domain and holding their own issue");
    var domain = ExternalSkillCategory.of("domain", null, ESkillKind.DOMAIN);
    var firstIssue = ExternalSkillCategory.of("first issue", domain, ESkillKind.ISSUE);
    var secondIssue = ExternalSkillCategory.of("second issue", domain, ESkillKind.ISSUE);

    var firstSkill = skill("first skill", "CODE_1", firstIssue, ESkillReferentialType.XXI);
    var secondSkill = skill("second skill", "CODE_2", secondIssue, ESkillReferentialType.XXI);

    when(externalSkillRepository.findAllWithCategoriesByType(ESkillReferentialType.XXI))
        .thenReturn(List.of(firstSkill, secondSkill));

    BddLogger.when("getting the catalog");
    var catalog = service.getCatalog(ESkillReferentialType.XXI);

    BddLogger.then("the domain is emitted once, before the issues and the skills");
    assertThat(catalog.type()).isEqualTo(ESkillReferentialType.XXI);
    assertThat(catalog.nodes()).hasSize(5);

    assertThat(catalog.nodes().stream().map(SkillNodeDTO::title))
        .containsExactly("domain", "first issue", "second issue", "first skill", "second skill");

    assertThat(nodeOf(catalog.nodes(), "domain").parentId()).isNull();
    assertThat(nodeOf(catalog.nodes(), "domain").assessable()).isFalse();
    assertThat(nodeOf(catalog.nodes(), "domain").kind()).isEqualTo(ESkillKind.DOMAIN);

    assertThat(nodeOf(catalog.nodes(), "first issue").parentId()).isEqualTo(domain.getId());
    assertThat(nodeOf(catalog.nodes(), "first issue").assessable()).isFalse();

    assertThat(nodeOf(catalog.nodes(), "first skill").parentId()).isEqualTo(firstIssue.getId());
    assertThat(nodeOf(catalog.nodes(), "first skill").externalCode()).isEqualTo("CODE_1");
    assertThat(nodeOf(catalog.nodes(), "first skill").assessable()).isTrue();
    assertThat(nodeOf(catalog.nodes(), "first skill").kind()).isNull();
  }

  @Test
  void shouldReturnAnEmptyCatalogWhenNoSkillIsImported() {
    BddLogger.given("no imported skill");
    when(externalSkillRepository.findAllWithCategoriesByType(ESkillReferentialType.CASOC))
        .thenReturn(List.of());

    BddLogger.when("getting the catalog");
    var catalog = service.getCatalog(ESkillReferentialType.CASOC);

    BddLogger.then("the catalog holds no node");
    assertThat(catalog.nodes()).isEmpty();
    assertThat(catalog.externalId()).isEqualTo("CASOC");
  }

  @Test
  void shouldEmitSkillsWithoutCategoryAsRootNodes() {
    BddLogger.given("a skill without category");
    var orphanSkill = skill("orphan skill", "CODE_3", null, ESkillReferentialType.CASOL);

    when(externalSkillRepository.findAllWithCategoriesByType(ESkillReferentialType.CASOL))
        .thenReturn(List.of(orphanSkill));

    BddLogger.when("getting the catalog");
    var catalog = service.getCatalog(ESkillReferentialType.CASOL);

    BddLogger.then("the skill is a root assessable node");
    assertThat(catalog.nodes()).hasSize(1);
    assertThat(catalog.nodes().getFirst().parentId()).isNull();
    assertThat(catalog.nodes().getFirst().assessable()).isTrue();
  }

  private ExternalSkill skill(
      String libelle,
      String externalCode,
      ExternalSkillCategory category,
      ESkillReferentialType type) {
    return ExternalSkill.create(UUID.randomUUID(), libelle, externalCode, category, type);
  }

  private SkillNodeDTO nodeOf(List<SkillNodeDTO> nodes, String title) {
    return nodes.stream()
        .filter(node -> node.title().equals(title))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No node titled " + title));
  }
}
