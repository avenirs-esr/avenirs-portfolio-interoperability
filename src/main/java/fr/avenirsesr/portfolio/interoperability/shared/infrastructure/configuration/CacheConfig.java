package fr.avenirsesr.portfolio.interoperability.shared.infrastructure.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkillPagedResult;
import fr.avenirsesr.portfolio.interoperability.externalskill.infrastructure.adapter.opensearch.ExternalSkillConstants;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
@EnableCaching
public class CacheConfig {

  @Bean
  public CacheManager externalSkillCacheManager(RedisConnectionFactory factory) {
    ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    Jackson2JsonRedisSerializer<ExternalSkillPagedResult> serializer =
        new Jackson2JsonRedisSerializer<>(mapper, ExternalSkillPagedResult.class);

    RedisCacheConfiguration config =
        RedisCacheConfiguration.defaultCacheConfig()
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(serializer));

    return RedisCacheManager.builder(factory)
        .withCacheConfiguration(ExternalSkillConstants.INDEX, config)
        .build();
  }
}
