package fr.avenirsesr.portfolio.interoperability.externaluser.domain.port.input;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.interoperability.externaluser.domain.model.ExternalUser;
import fr.avenirsesr.portfolio.interoperability.externaluser.domain.model.enums.EExternalSource;
import fr.avenirsesr.portfolio.interoperability.externaluser.domain.model.enums.EExternalUserStatus;
import java.util.UUID;

public interface ExternalUserService {

  ExternalUser importExternalUser(
      UUID userId,
      String firstName,
      String lastName,
      String email,
      EUserCategory category,
      String externalId,
      EExternalSource source,
      EExternalUserStatus status);
}
