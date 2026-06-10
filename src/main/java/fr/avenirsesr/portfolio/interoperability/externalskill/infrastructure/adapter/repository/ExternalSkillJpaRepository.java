package fr.avenirsesr.portfolio.interoperability.externalskill.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.interoperability.externalskill.infrastructure.adapter.model.ExternalSkillEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExternalSkillJpaRepository
    extends JpaRepository<ExternalSkillEntity, UUID>,
        JpaSpecificationExecutor<ExternalSkillEntity> {

  @Query(
      value = "SELECT * FROM external_skill ORDER BY md5(id::text) LIMIT :limit",
      nativeQuery = true)
  List<ExternalSkillEntity> findRandom(@Param("limit") int limit);

  @Query(
      """
      select distinct es
      from ExternalSkillEntity es
      left join fetch es.externalSkillCategory c
      left join fetch c.parent p1
      left join fetch p1.parent p2
      left join fetch p2.parent p3
      """)
  List<ExternalSkillEntity> findAllWithCategories();
}
