package fr.avenirsesr.portfolio.interoperability.externaluser.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.interoperability.externaluser.domain.model.enums.EExternalSource;
import fr.avenirsesr.portfolio.interoperability.externaluser.domain.model.enums.EExternalUserStatus;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;

@Getter
public class ExternalUser extends AvenirsBaseModel {

  private final String externalId;
  private final EExternalSource source;
  private final UUID userId;
  private final EUserCategory category;
  private final String email;
  private final String firstName;
  private final String lastName;
  private final EExternalUserStatus status;

  private ExternalUser(
      UUID id,
      Instant createdAt,
      Instant updatedAt,
      String externalId,
      EExternalSource source,
      UUID userId,
      EUserCategory category,
      String email,
      String firstName,
      String lastName,
      EExternalUserStatus status) {
    super(id, createdAt, updatedAt);
    this.externalId = externalId;
    this.source = source;
    this.userId = userId;
    this.category = category;
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
    this.status = status;
  }

  public static ExternalUser create(
      UUID userId,
      String externalId,
      EExternalSource source,
      EUserCategory category,
      String email,
      String firstName,
      String lastName,
      EExternalUserStatus status) {
    Instant now = Instant.now();

    return new ExternalUser(
        UUID.randomUUID(),
        now,
        now,
        externalId,
        source,
        userId,
        category,
        email,
        firstName,
        lastName,
        status);
  }

  public static ExternalUser toDomain(
      UUID id,
      Instant createdAt,
      Instant updatedAt,
      String externalId,
      EExternalSource source,
      UUID userId,
      EUserCategory category,
      String email,
      String firstName,
      String lastName,
      EExternalUserStatus status) {
    return new ExternalUser(
        id,
        createdAt,
        updatedAt,
        externalId,
        source,
        userId,
        category,
        email,
        firstName,
        lastName,
        status);
  }

  public ExternalUser linkToUser(UUID userId) {
    return new ExternalUser(
        getId(),
        getCreatedAt(),
        Instant.now(),
        externalId,
        source,
        userId,
        category,
        email,
        firstName,
        lastName,
        status);
  }

  public boolean isLinkedToUser() {
    return userId != null;
  }

  public boolean isActive() {
    return EExternalUserStatus.ACTIVE.equals(status);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ExternalUser that = (ExternalUser) o;
    return Objects.equals(externalId, that.externalId) && source == that.source;
  }

  @Override
  public int hashCode() {
    return Objects.hash(externalId, source);
  }

  @Override
  public String toString() {
    return "ExternalUser[source=" + source + ", externalId=" + externalId + ']';
  }
}
