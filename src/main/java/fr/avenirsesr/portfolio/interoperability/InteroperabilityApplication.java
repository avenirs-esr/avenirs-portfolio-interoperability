package fr.avenirsesr.portfolio.interoperability;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication(
    scanBasePackages = {
      "fr.avenirsesr.portfolio.interoperability",
      "fr.avenirsesr.portfolio.common"
    })
public class InteroperabilityApplication {

  public static void main(String[] args) {
    SpringApplication.run(InteroperabilityApplication.class, args);
  }
}
