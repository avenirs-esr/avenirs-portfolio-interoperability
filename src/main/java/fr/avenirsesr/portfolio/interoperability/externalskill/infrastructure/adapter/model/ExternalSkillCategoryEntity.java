package fr.avenirsesr.portfolio.interoperability.externalskill.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillCategoryType;
import jakarta.persistence.*;
import jakarta.persistence.Index;
import java.util.Optional;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(
    name = "external_skill_category",
    indexes = {@Index(name = "idx_external_skill_category_parent_id", columnList = "parent_id")})
public class ExternalSkillCategoryEntity extends AvenirsBaseEntity {
  @Column(nullable = false)
  private String libelle;

  @Column
  @Enumerated(EnumType.STRING)
  private EExternalSkillCategoryType type;

  @Getter(AccessLevel.NONE)
  @ManyToOne(
      fetch = FetchType.LAZY,
      cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  private ExternalSkillCategoryEntity parent;

  private ExternalSkillCategoryEntity(
      UUID id,
      String libelle,
      EExternalSkillCategoryType type,
      ExternalSkillCategoryEntity parent) {
    setId(id);
    this.libelle = libelle;
    this.type = type;
    this.parent = parent;
  }

  public static ExternalSkillCategoryEntity of(
      UUID id,
      String libelle,
      EExternalSkillCategoryType type,
      ExternalSkillCategoryEntity parent) {
    return new ExternalSkillCategoryEntity(id, libelle, type, parent);
  }

  public static ExternalSkillCategoryEntity create(
      String libelle, EExternalSkillCategoryType type, ExternalSkillCategoryEntity parent) {
    return new ExternalSkillCategoryEntity(UUID.randomUUID(), libelle, type, parent);
  }

  public Optional<ExternalSkillCategoryEntity> getParent() {
    return Optional.ofNullable(parent);
  }
}
