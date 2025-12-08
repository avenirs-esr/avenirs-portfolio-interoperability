package fr.avenirsesr.portfolio.interoperability.externalskill.rome.infrastructure.batch;

import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkill;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.model.ExternalSkillCategory;
import fr.avenirsesr.portfolio.interoperability.externalskill.domain.port.output.repository.ExternalSkillRepository;
import fr.avenirsesr.portfolio.interoperability.externalskill.rome.domain.model.Competence;
import fr.avenirsesr.portfolio.interoperability.externalskill.rome.domain.model.mapper.CompetenceMapper;
import fr.avenirsesr.portfolio.interoperability.externalskill.rome.domain.port.input.RomeExternalSkillService;
import fr.avenirsesr.portfolio.interoperability.externalskill.rome.domain.port.output.RomeExternalSkillApi;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@Profile("!test")
@RequiredArgsConstructor
public class ExternalSkillBatchLoader {
  private final RomeExternalSkillApi romeAdditionalSkillApi;
  private final RomeExternalSkillService romeAdditionalSkillService;
  private final ExternalSkillRepository externalSkillRepository;
  private final JdbcTemplate jdbcTemplate;

  @Bean
  public Job importROME4SkillJob(JobRepository jobRepository, Flow importROME4SkillFlow) {
    return new JobBuilder("importROME4SkillJob", jobRepository)
        .incrementer(new RunIdIncrementer())
        .start(importROME4SkillFlow)
        .end()
        .listener(jobResultListener())
        .build();
  }

  @Bean
  public Flow importROME4SkillFlow(
      Step checkAdditionalSkillCountStep,
      Step checkROME4VersionUpdateStep,
      Step importROME4SkillStep) {
    return new FlowBuilder<SimpleFlow>("importROME4SkillFlow")
        .start(checkAdditionalSkillCountStep)
        .on("NOOP")
        .end()
        .from(checkAdditionalSkillCountStep)
        .on("*")
        .to(checkROME4VersionUpdateStep)
        .from(checkROME4VersionUpdateStep)
        .on("NOOP")
        .end()
        .from(checkROME4VersionUpdateStep)
        .on("*")
        .to(importROME4SkillStep)
        .end();
  }

  @Bean
  public Step checkAdditionalSkillCountStep(
      JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    return new StepBuilder("checkAdditionalSkillCountStep", jobRepository)
        .tasklet(
            (contribution, chunkContext) -> {
              int count = externalSkillRepository.countAll(EExternalSkillType.ROME4);

              if (count == 0) {
                log.info(
                    "No additional skills found, bootstrapping from SQL dump and skipping the rest"
                        + " of the job.");
                bootstrapAdditionalSkillsFromSqlDump();
                romeAdditionalSkillService.cleanAndCreateExternalSkillIndex();
              } else {
                log.info("{} Additional skills found, continuing with sync job.", count);
              }

              return RepeatStatus.FINISHED;
            },
            transactionManager)
        .build();
  }

  @Bean
  public Step checkROME4VersionUpdateStep(
      JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    return new StepBuilder("checkROME4VersionUpdateStep", jobRepository)
        .tasklet(
            (contribution, chunkContext) -> {
              boolean isNewVersion = romeAdditionalSkillService.checkRomeVersionUpdated();

              if (!isNewVersion) {
                log.info(
                    "checkROME4VersionUpdateStep (NOOP) because there are no updates to ROME 4.0");
                contribution.setExitStatus(ExitStatus.NOOP);
              } else {
                log.info(
                    "checkROME4VersionUpdateStep (COMPLETED) because there are updates to ROME"
                        + " 4.0");
              }

              return RepeatStatus.FINISHED;
            },
            transactionManager)
        .build();
  }

  @Bean
  public Step importROME4SkillStep(
      JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    var categories = new ArrayList<ExternalSkillCategory>();
    return new StepBuilder("importROME4SkillStep", jobRepository)
        .<Competence, ExternalSkill>chunk(100, transactionManager)
        .reader(itemReader())
        .processor(itemProcessor(categories))
        .writer(itemWriter())
        .faultTolerant()
        .skipPolicy(
            (throwable, skipCount) -> {
              log.error("Error while importing skills", throwable);
              return throwable instanceof RuntimeException;
            })
        .listener(skipListener())
        .build();
  }

  @Bean
  @StepScope
  public ItemReader<Competence> itemReader() {
    return new ItemReader<>() {
      private Iterator<Competence> iterator;

      @Override
      public Competence read() {
        if (iterator == null) {
          try {
            List<Competence> data = romeAdditionalSkillApi.fetchAdditionalSkills();
            romeAdditionalSkillService.cleanAndCreateExternalSkillIndex();
            iterator = data.iterator();
          } catch (Exception e) {
            log.error("Error ROME4.0 API : {}", e.getMessage());
            iterator = Collections.emptyIterator();
          }
        }
        return iterator.hasNext() ? iterator.next() : null;
      }
    };
  }

  @Bean
  public ItemProcessor<Competence, ExternalSkill> itemProcessor(
      ArrayList<ExternalSkillCategory> categories) {
    return (Competence competence) -> {
      return ExternalSkill.create(
          competence.getLibelle(),
          competence.getCode(),
          CompetenceMapper.toCategoryDomain(competence, categories),
          EExternalSkillType.ROME4);
    };
  }

  @Bean
  public ItemWriter<ExternalSkill> itemWriter() {
    return externalSkills -> {
      List<ExternalSkill> externalSkillList = new ArrayList<>(externalSkills.getItems());
      romeAdditionalSkillService.synchronizeExternalSkills(externalSkillList);
    };
  }

  @Bean
  public SkipListener<Competence, ExternalSkill> skipListener() {
    return new SkipListener<>() {
      @Override
      public void onSkipInRead(Throwable t) {
        log.error("Skip in reading (API) : {}", t.getMessage());
      }

      @Override
      public void onSkipInProcess(Competence item, Throwable t) {
        log.error("Skip in processing for {} : {}", item, t.getMessage());
      }

      @Override
      public void onSkipInWrite(ExternalSkill item, Throwable t) {
        log.error("Skip in writing for {} : {}", item, t.getMessage());
      }
    };
  }

  @Bean
  public JobExecutionListener jobResultListener() {
    return new JobExecutionListener() {
      @Override
      public void afterJob(JobExecution jobExecution) {
        Predicate<StepExecution> condition =
            stepExec ->
                stepExec.getReadCount() == 0
                    && stepExec.getWriteCount() == 0
                    && stepExec.getSkipCount() >= 0;

        List<StepExecution> filtered =
            jobExecution.getStepExecutions().stream()
                .filter(
                    stepExec ->
                        !"checkROME4VersionUpdateStep".equals(stepExec.getStepName())
                            || stepExec.getExitStatus().compareTo(ExitStatus.NOOP) != 0)
                .toList();

        boolean allSkippedOrEmpty = !filtered.isEmpty() && filtered.stream().allMatch(condition);

        if (allSkippedOrEmpty) {
          jobExecution.setStatus(BatchStatus.FAILED);
          jobExecution.setExitStatus(
              new ExitStatus("FAILED", "All steps skipped, no data processed"));
          log.error("Job completed as FAILED because all steps were skipped/empty.");
        }
      }
    };
  }

  public void bootstrapAdditionalSkillsFromSqlDump() {
    Resource additionalSkillsCategoriesResource =
        new ClassPathResource("/external-skill/rome/rome_4_external_skill_category.sql");
    Resource additionalSkillsResource =
        new ClassPathResource("/external-skill/rome/rome_4_external_skill.sql");
    Resource rome4versionResource =
        new ClassPathResource("/external-skill/rome/rome_4_version.sql");

    try {
      String additionalSkillsCategoriesSql =
          new String(
              additionalSkillsCategoriesResource.getInputStream().readAllBytes(),
              StandardCharsets.UTF_8);
      String additionalSkillsSql =
          new String(
              additionalSkillsResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
      String rome4versionSql =
          new String(rome4versionResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

      jdbcTemplate.execute(additionalSkillsCategoriesSql);
      jdbcTemplate.execute(additionalSkillsSql);
      jdbcTemplate.execute(rome4versionSql);

      log.info("additional skills bootstrap successfully executed.");
    } catch (IOException e) {
      log.error("An error occurred while bootstrapping ROME 4.0 version.", e);
      throw new RuntimeException(e);
    }
  }
}
