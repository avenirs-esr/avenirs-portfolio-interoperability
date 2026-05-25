package fr.avenirsesr.portfolio.interoperability.externaluser.domain.service;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.user.domain.model.enums.EUserStatus;
import fr.avenirsesr.portfolio.interoperability.externaluser.domain.model.ExternalUser;
import fr.avenirsesr.portfolio.interoperability.externaluser.domain.model.enums.EExternalSource;
import fr.avenirsesr.portfolio.interoperability.externaluser.domain.port.input.ExternalUserService;
import fr.avenirsesr.portfolio.interoperability.externaluser.domain.port.output.repository.ExternalUserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class ExternalUserServiceImpl implements ExternalUserService {

  private final ExternalUserRepository externalUserRepository;

  @Override
  public ExternalUser importExternalUser(
      String eppn,
      String firstName,
      String lastName,
      String email,
      EUserCategory category,
      String externalId,
      EExternalSource source,
      EUserStatus status) {

    var externalUser =
        ExternalUser.create(
            eppn,
            externalId,
            source,
            category,
            email,
            firstName,
            lastName,
            status != null ? status : EUserStatus.ACTIVE);

    externalUserRepository.save(externalUser);

    return externalUser;
  }
}
