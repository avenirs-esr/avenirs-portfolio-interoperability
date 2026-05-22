package fr.avenirsesr.portfolio.interoperability.externaluser.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.interoperability.externaluser.domain.model.ExternalUser;
import fr.avenirsesr.portfolio.interoperability.externaluser.domain.port.output.repository.ExternalUserRepository;
import fr.avenirsesr.portfolio.interoperability.externaluser.infrastructure.adapter.mapper.ExternalUserMapper;
import fr.avenirsesr.portfolio.interoperability.externaluser.infrastructure.adapter.model.ExternalUserEntity;
import org.springframework.stereotype.Component;

@Component
public class ExternalUserDatabaseRepository
    extends GenericJpaRepositoryAdapter<ExternalUser, ExternalUserEntity>
    implements ExternalUserRepository {

  public ExternalUserDatabaseRepository(ExternalUserJpaRepository jpaRepository) {
    super(jpaRepository, jpaRepository, ExternalUserEntity.class, ExternalUserMapper.INSTANCE);
  }
}
