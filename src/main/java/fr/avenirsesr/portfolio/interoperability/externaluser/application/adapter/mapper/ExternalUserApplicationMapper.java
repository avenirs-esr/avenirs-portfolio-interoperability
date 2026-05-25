package fr.avenirsesr.portfolio.interoperability.externaluser.application.adapter.mapper;

import fr.avenirsesr.portfolio.common.user.application.adapter.dto.ExternalUserDTO;
import fr.avenirsesr.portfolio.interoperability.externaluser.domain.model.ExternalUser;

public class ExternalUserApplicationMapper {

  private ExternalUserApplicationMapper() {}

  public static ExternalUserDTO toExternalUserDTO(ExternalUser externalUser) {
    return new ExternalUserDTO(
        externalUser.getEppn(),
        externalUser.getFirstName(),
        externalUser.getLastName(),
        externalUser.getEmail(),
        externalUser.getCategory(),
        externalUser.getExternalId(),
        externalUser.getSource().name(),
        externalUser.getStatus());
  }
}
