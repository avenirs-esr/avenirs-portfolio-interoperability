package fr.avenirsesr.portfolio.interoperability.externaluser.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.interoperability.externaluser.domain.model.enums.EExternalSource;
import fr.avenirsesr.portfolio.interoperability.externaluser.domain.model.enums.EExternalUserStatus;
import java.util.UUID;

public record ExternalUserCreationData(
    UUID userId,
    String firstName,
    String lastName,
    String email,
    EUserCategory category,
    String externalId,
    EExternalSource source,
    EExternalUserStatus status) {}
