package ua.nure.ice.bookcatalog.laboratorna3.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "ua.nure.ice.bookcatalog.laboratorna3")
public class App {
  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }
}
