package ua.nure.ice.bookcatalog.laboratorna3.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "ua.nure.ice.bookcatalog.laboratorna3")
@EntityScan(basePackages = "ua.nure.ice.bookcatalog.laboratorna3.model")
@EnableJpaRepositories(basePackages = "ua.nure.ice.bookcatalog.laboratorna3.repository")
public class App {
  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }
}
