package fr.avenirsesr.portfolio.interoperability.externaluser.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.user.domain.model.enums.EUserStatus;
import fr.avenirsesr.portfolio.interoperability.externaluser.domain.model.enums.EExternalSource;

public record ExternalUserCreationData(
    String eppn,
    String firstName,
    String lastName,
    String email,
    EUserCategory category,
    String externalId,
    EExternalSource source,
    EUserStatus status) {}
