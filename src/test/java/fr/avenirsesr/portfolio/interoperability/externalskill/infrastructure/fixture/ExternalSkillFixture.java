package fr.avenirsesr.portfolio.interoperability.externalskill.infrastructure.fixture;

import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkill;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkillCategory;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExternalSkillFixture {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(ExternalSkillFixture.class, SharedDataGenerator.class);

  private UUID id;
  private String libelle;
  private ExternalSkillCategory externalSkillCategory;
  private EExternalSkillType type;
  private Instant createdAt;
  private Instant updatedAt;

  private ExternalSkillFixture() {
    this.id = dataGenerator.with("id").uuid();
    this.externalSkillCategory = null;
    this.type = EExternalSkillType.ROME4;
    this.libelle = "libelle";
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
  }

  public static ExternalSkillFixture create() {
    return new ExternalSkillFixture();
  }

  public ExternalSkillFixture withId(UUID id) {
    this.id = id;
    return this;
  }

  public ExternalSkillFixture withLibelle(String libelle) {
    this.libelle = libelle;
    return this;
  }

  public ExternalSkillFixture withCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public ExternalSkillFixture withUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  public ExternalSkillFixture withCategory(ExternalSkillCategory externalSkillCategory) {
    this.externalSkillCategory = externalSkillCategory;
    return this;
  }

  public ExternalSkillFixture withType(EExternalSkillType type) {
    this.type = type;
    return this;
  }

  public static List<ExternalSkillFixture> create(int count) {
    List<ExternalSkillFixture> externalSkillFixtureList = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      externalSkillFixtureList.add(create());
    }
    return externalSkillFixtureList;
  }

  public ExternalSkill toModel() {
    return ExternalSkill.toDomain(id, libelle, externalSkillCategory, type, createdAt, updatedAt);
  }
}
