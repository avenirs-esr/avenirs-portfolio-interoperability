package fr.avenirsesr.portfolio.interoperability.externalskill.infrastructure.adapter.opensearch;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillCategoryType;
import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkill;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkillCategory;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkillPagedResult;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.output.OpenSearchIndex;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.action.admin.indices.delete.DeleteIndexRequest;
import org.opensearch.action.bulk.BulkRequest;
import org.opensearch.action.bulk.BulkResponse;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.client.indices.CreateIndexRequest;
import org.opensearch.client.indices.GetIndexRequest;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@Profile("!test")
public class OpenSearchIndexImpl implements OpenSearchIndex {
  private final RestHighLevelClient client;

  @Override
  @CacheEvict(value = ExternalSkillConstants.INDEX)
  public void cleanAndCreateExternalSkillIndex() {
    try {
      if (client
          .indices()
          .exists(new GetIndexRequest(ExternalSkillConstants.INDEX), RequestOptions.DEFAULT)) {
        client
            .indices()
            .delete(new DeleteIndexRequest(ExternalSkillConstants.INDEX), RequestOptions.DEFAULT);
        log.info("Index '{}' deleted.", ExternalSkillConstants.INDEX);
      }

      client
          .indices()
          .create(new CreateIndexRequest(ExternalSkillConstants.INDEX), RequestOptions.DEFAULT);
      log.info("Index '{}' created.", ExternalSkillConstants.INDEX);
    } catch (IOException e) {
      throw new RuntimeException("Failed to recreate index: " + ExternalSkillConstants.INDEX, e);
    }
  }

  @Override
  public void indexAll(List<ExternalSkill> externalSkillList) {
    BulkRequest bulkRequest = new BulkRequest();

    for (ExternalSkill externalSkill : externalSkillList) {
      try {
        Map<String, Object> source = getExternalSkillSourceMap(externalSkill);
        bulkRequest.add(
            new IndexRequest(ExternalSkillConstants.INDEX)
                .id(externalSkill.getId().toString())
                .source(source));
      } catch (Exception e) {
        throw new RuntimeException("Failed externalSkill: " + externalSkill.getId(), e);
      }
    }

    try {
      BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
      if (response.hasFailures()) {
        log.error("Bulk indexing errors: {}", response.buildFailureMessage());
      }
    } catch (IOException e) {
      throw new RuntimeException("OpenSearchIndex bulk indexing failed", e);
    } finally {
      log.info("Bulk indexing succeeded for {} documents", externalSkillList.size());
    }
  }

  @Override
  @Cacheable(
      value = ExternalSkillConstants.INDEX,
      key = "#keyword + '_' + #pageCriteria.page() + '_' + #pageCriteria.pageSize()")
  public ExternalSkillPagedResult search(String keyword, PageCriteria pageCriteria) {
    SearchRequest searchRequest = new SearchRequest(ExternalSkillConstants.INDEX);

    SearchSourceBuilder sourceBuilder =
        new SearchSourceBuilder()
            .query(
                QueryBuilders.matchPhrasePrefixQuery(
                    ExternalSkillConstants.FIELD_SKILL_LIBELLE, keyword))
            .from(pageCriteria.page() * pageCriteria.pageSize())
            .size(pageCriteria.pageSize())
            .trackTotalHits(true);

    searchRequest.source(sourceBuilder);

    try {
      SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
      long totalHits = response.getHits().getTotalHits().value;

      List<ExternalSkill> externalSkillList = getExternalSkillList(response);

      return new ExternalSkillPagedResult(
          externalSkillList, new PageInfo(pageCriteria.page(), pageCriteria.pageSize(), totalHits));
    } catch (IOException e) {
      throw new RuntimeException(
          "OpenSearchIndex search failed with param keyword="
              + keyword
              + ", page="
              + pageCriteria.page()
              + ", size="
              + pageCriteria.pageSize(),
          e);
    }
  }

  private Map<String, Object> getExternalSkillSourceMap(ExternalSkill externalSkill) {
    var map = new HashMap<String, Object>();
    map.put(ExternalSkillConstants.FIELD_ID, externalSkill.getId().toString());
    map.put(ExternalSkillConstants.FIELD_SKILL_LIBELLE, externalSkill.getLibelle());
    map.put(ExternalSkillConstants.FIELD_TYPE, externalSkill.getType());
    map.put(
        ExternalSkillConstants.FIELD_SKILL_CATEGORIES,
        externalSkill.getExternalSkillCategory().map(this::getCategorySource).orElse(null));

    return map;
  }

  private List<ExternalSkill> getExternalSkillList(SearchResponse response) {
    return Arrays.stream(response.getHits().getHits())
        .map(
            hit -> {
              Map<String, Object> src = hit.getSourceAsMap();
              return ExternalSkill.toDomain(
                  UUID.fromString((String) src.get(ExternalSkillConstants.FIELD_ID)),
                  (String) src.get(ExternalSkillConstants.FIELD_SKILL_LIBELLE),
                  getCategoryFromSource(src.get(ExternalSkillConstants.FIELD_SKILL_CATEGORIES)),
                  EExternalSkillType.valueOf((String) src.get(ExternalSkillConstants.FIELD_TYPE)),
                  Instant.now(),
                  Instant.now());
            })
        .toList();
  }

  private Map<String, Object> getCategorySource(ExternalSkillCategory externalSkillCategory) {
    if (externalSkillCategory == null) return null;

    Map<String, Object> map = new HashMap<>();
    map.put("id", externalSkillCategory.getId().toString());
    map.put("libelle", externalSkillCategory.getLibelle());
    map.put("type", externalSkillCategory.getType());
    if (externalSkillCategory.getParent().isPresent()) {
      map.put("parent", getCategorySource(externalSkillCategory.getParent().get()));
    }
    return map;
  }

  @SuppressWarnings("unchecked")
  private ExternalSkillCategory getCategoryFromSource(Object source) {
    if (source == null) return null;

    Map<String, Object> map = (Map<String, Object>) source;
    UUID id = map.containsKey("id") ? UUID.fromString((String) map.get("id")) : null;
    String libelle = (String) map.get("libelle");
    EExternalSkillCategoryType type =
        EExternalSkillCategoryType.valueOf(map.get("type").toString());

    ExternalSkillCategory parent = null;
    if (map.containsKey("parent")) {
      parent = getCategoryFromSource(map.get("parent"));
    }

    return ExternalSkillCategory.toDomain(id, libelle, parent, type, null, null);
  }
}
