package fr.avenirsesr.portfolio.interoperability.externalskill.rome.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.interoperability.externalskill.rome.domain.model.Rome4Version;
import fr.avenirsesr.portfolio.interoperability.externalskill.rome.infrastructure.adapter.model.Rome4VersionEntity;

public class Rome4VersionMapper implements Mapper<Rome4VersionEntity, Rome4Version> {

  public static final Rome4VersionMapper INSTANCE = new Rome4VersionMapper();

  @Override
  public Rome4VersionEntity fromDomain(Rome4Version rome4Version) {
    return Rome4VersionEntity.of(
        rome4Version.getId(), rome4Version.getVersion(), rome4Version.getLastModifiedDate());
  }

  @Override
  public Rome4Version toDomain(Rome4VersionEntity entity) {
    return Rome4Version.toDomain(entity.getId(), entity.getVersion(), entity.getLastModifiedDate());
  }
}
