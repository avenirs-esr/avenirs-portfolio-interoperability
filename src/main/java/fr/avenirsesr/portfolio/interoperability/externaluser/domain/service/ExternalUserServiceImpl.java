package fr.avenirsesr.portfolio.interoperability.externaluser.domain.service;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.interoperability.externaluser.domain.model.ExternalUser;
import fr.avenirsesr.portfolio.interoperability.externaluser.domain.model.enums.EExternalSource;
import fr.avenirsesr.portfolio.interoperability.externaluser.domain.model.enums.EExternalUserStatus;
import fr.avenirsesr.portfolio.interoperability.externaluser.domain.port.input.ExternalUserService;
import fr.avenirsesr.portfolio.interoperability.externaluser.domain.port.output.repository.ExternalUserRepository;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class ExternalUserServiceImpl implements ExternalUserService {

  private final ExternalUserRepository externalUserRepository;

  @Override
  public ExternalUser importExternalUser(
      UUID userId,
      String firstName,
      String lastName,
      String email,
      EUserCategory category,
      String externalId,
      EExternalSource source,
      EExternalUserStatus status) {

    var externalUser =
        ExternalUser.create(
            userId,
            externalId,
            source,
            category,
            email,
            firstName,
            lastName,
            status != null ? status : EExternalUserStatus.ACTIVE);

    externalUserRepository.save(externalUser);

    return externalUser;
  }
}
