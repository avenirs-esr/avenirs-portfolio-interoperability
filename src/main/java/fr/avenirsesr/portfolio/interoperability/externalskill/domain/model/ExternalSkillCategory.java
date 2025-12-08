package fr.avenirsesr.portfolio.interoperability.externalskill.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillCategoryType;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;

public class ExternalSkillCategory extends AvenirsBaseModel {
  @Getter private final String libelle;
  @Getter private final EExternalSkillCategoryType type;
  private final ExternalSkillCategory parent;

  private ExternalSkillCategory(
      UUID id,
      String libelle,
      ExternalSkillCategory parent,
      EExternalSkillCategoryType type,
      Instant createdAt,
      Instant updatedAt) {
    super(id, createdAt, updatedAt);
    this.libelle = libelle;
    this.parent = parent;
    this.type = type;
  }

  public static ExternalSkillCategory of(
      String libelle, ExternalSkillCategory parent, EExternalSkillCategoryType type) {
    return new ExternalSkillCategory(
        UUID.randomUUID(), libelle, parent, type, Instant.now(), Instant.now());
  }

  public static ExternalSkillCategory toDomain(
      UUID id,
      String libelle,
      ExternalSkillCategory parent,
      EExternalSkillCategoryType type,
      Instant createdAt,
      Instant updatedAt) {
    return new ExternalSkillCategory(id, libelle, parent, type, createdAt, updatedAt);
  }

  public Optional<ExternalSkillCategory> getParent() {
    return Optional.ofNullable(parent);
  }

  public long uniqHash() {
    return libelle.hashCode() + type.hashCode() + (parent == null ? 0 : parent.hashCode());
  }
}
