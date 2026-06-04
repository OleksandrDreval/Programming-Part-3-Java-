package ua.nure.ice.bookcatalog.laboratorna4.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "ua.nure.ice.bookcatalog.laboratorna4")
@EntityScan(basePackages = "ua.nure.ice.bookcatalog.laboratorna4.model")
@EnableJpaRepositories(basePackages = "ua.nure.ice.bookcatalog.laboratorna4.repository")
public class App {
  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }
}
