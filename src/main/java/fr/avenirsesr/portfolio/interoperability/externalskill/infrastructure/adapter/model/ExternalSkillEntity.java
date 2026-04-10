package fr.avenirsesr.portfolio.interoperability.externalskill.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import jakarta.persistence.*;
import java.util.Optional;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "external_skill",
    indexes = {
      @Index(name = "idx_external_skill_external_id", columnList = "external_id"),
      @Index(name = "idx_external_skill_category_id", columnList = "external_skill_category_id")
    })
@NoArgsConstructor
@Getter
@Setter
public class ExternalSkillEntity extends AvenirsBaseEntity {

  @Column(nullable = false, name = "external_id")
  private String externalId;

  @Column(nullable = false)
  private String libelle;

  @Enumerated(EnumType.STRING)
  private EExternalSkillType type;

  @Getter(AccessLevel.NONE)
  @ManyToOne(
      fetch = FetchType.LAZY,
      cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinColumn(name = "external_skill_category_id")
  private ExternalSkillCategoryEntity externalSkillCategory;

  private ExternalSkillEntity(
      UUID id,
      String externalId,
      String libelle,
      EExternalSkillType type,
      ExternalSkillCategoryEntity externalSkillCategory) {
    setId(id);
    this.type = type;
    this.externalSkillCategory = externalSkillCategory;
    this.externalId = externalId;
    this.libelle = libelle;
  }

  public static ExternalSkillEntity of(
      UUID id,
      String externalId,
      String libelle,
      EExternalSkillType type,
      ExternalSkillCategoryEntity externalSkillCategory) {
    return new ExternalSkillEntity(id, externalId, libelle, type, externalSkillCategory);
  }

  public static ExternalSkillEntity create(
      String externalId,
      String libelle,
      EExternalSkillType type,
      ExternalSkillCategoryEntity externalSkillCategory) {
    return new ExternalSkillEntity(
        UUID.randomUUID(), externalId, libelle, type, externalSkillCategory);
  }

  public Optional<ExternalSkillCategoryEntity> getExternalSkillCategory() {
    return Optional.ofNullable(externalSkillCategory);
  }
}
