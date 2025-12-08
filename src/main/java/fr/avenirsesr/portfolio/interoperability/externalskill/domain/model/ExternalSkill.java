package fr.avenirsesr.portfolio.interoperability.externalskill.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import java.time.Instant;
import java.util.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExternalSkill extends AvenirsBaseModel {
  @Getter(AccessLevel.NONE)
  private ExternalSkillCategory externalSkillCategory;

  private String libelle;
  private String externalId;
  private EExternalSkillType type;

  private ExternalSkill(
      UUID id,
      Instant createdAt,
      Instant updatedAt,
      ExternalSkillCategory externalSkillCategory,
      EExternalSkillType type,
      String libelle,
      String externalId) {
    super(id, createdAt, updatedAt);
    this.externalSkillCategory = externalSkillCategory;
    this.type = type;
    this.libelle = libelle;
    this.externalId = externalId;
  }

  public static ExternalSkill create(
      String libelle,
      String externalId,
      ExternalSkillCategory externalSkillCategory,
      EExternalSkillType type) {
    Instant now = Instant.now();
    return new ExternalSkill(
        UUID.randomUUID(), now, now, externalSkillCategory, type, libelle, externalId);
  }

  public static ExternalSkill toDomain(
      UUID id,
      String libelle,
      String externalId,
      ExternalSkillCategory externalSkillCategory,
      EExternalSkillType type,
      Instant createdAt,
      Instant updatedAt) {
    return new ExternalSkill(
        id, createdAt, updatedAt, externalSkillCategory, type, libelle, externalId);
  }

  public Optional<ExternalSkillCategory> getExternalSkillCategory() {
    return Optional.ofNullable(externalSkillCategory);
  }

  public List<ExternalSkillCategory> getCategoryPath() {
    List<ExternalSkillCategory> categories = new ArrayList<>();

    Optional<ExternalSkillCategory> current = getExternalSkillCategory();
    while (current.isPresent()) {
      categories.add(current.get());
      current = current.get().getParent();
    }

    Collections.reverse(categories);
    return categories;
  }
}
