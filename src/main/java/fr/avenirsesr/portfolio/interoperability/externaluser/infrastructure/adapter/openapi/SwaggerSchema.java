package fr.avenirsesr.portfolio.interoperability.externaluser.infrastructure.adapter.openapi;

import fr.avenirsesr.portfolio.interoperability.externaluser.domain.model.enums.EExternalSource;
import fr.avenirsesr.portfolio.interoperability.externaluser.domain.model.enums.EExternalUserStatus;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Arrays;

public final class SwaggerSchema {
  private SwaggerSchema() {}

  public static final Schema<String> userExternalSourceSchema =
      new StringSchema()
          .name("EExternalSource")
          ._enum(Arrays.stream(EExternalSource.values()).map(Enum::name).toList())
          .description("Enum for external source");

  public static final Schema<String> externalUserStatusSchema =
      new StringSchema()
          .name("EExternalUserStatus")
          ._enum(Arrays.stream(EExternalUserStatus.values()).map(Enum::name).toList())
          .description("Enum for external user status");
}
