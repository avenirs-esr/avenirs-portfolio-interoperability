package fr.avenirsesr.portfolio.interoperability.externaluser.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.interoperability.externaluser.domain.model.enums.EExternalSource;
import fr.avenirsesr.portfolio.interoperability.externaluser.domain.model.enums.EExternalUserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "external_user",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "external_user_external_id_source_uk",
          columnNames = {"external_id", "source"})
    },
    indexes = {
      @Index(name = "idx_ext_user_user_id", columnList = "user_id"),
      @Index(name = "idx_ext_user_email", columnList = "email"),
      @Index(name = "idx_ext_user_source_external_id", columnList = "source, external_id")
    })
@NoArgsConstructor
@Getter
@Setter
public class ExternalUserEntity extends AvenirsBaseEntity {

  @Column(nullable = false, name = "external_id")
  private String externalId;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private EExternalSource source;

  @Column(name = "user_id")
  private UUID userId;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private EUserCategory category;

  @Column(nullable = false)
  @Email
  private String email;

  @Column(nullable = false, name = "first_name")
  private String firstName;

  @Column(nullable = false, name = "last_name")
  private String lastName;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private EExternalUserStatus status;

  private ExternalUserEntity(
      UUID id,
      String externalId,
      EExternalSource source,
      UUID userId,
      EUserCategory category,
      String email,
      String firstName,
      String lastName,
      EExternalUserStatus status,
      Instant createdAt,
      Instant updatedAt) {
    this.setId(id);
    this.externalId = externalId;
    this.source = source;
    this.userId = userId;
    this.category = category;
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
    this.status = status;
    this.setCreatedAt(createdAt);
    this.setUpdatedAt(updatedAt);
  }

  public static ExternalUserEntity of(
      UUID id,
      String externalId,
      EExternalSource source,
      UUID userId,
      EUserCategory category,
      String email,
      String firstName,
      String lastName,
      EExternalUserStatus status,
      Instant createdAt,
      Instant updatedAt) {
    return new ExternalUserEntity(
        id,
        externalId,
        source,
        userId,
        category,
        email,
        firstName,
        lastName,
        status,
        createdAt,
        updatedAt);
  }
}
