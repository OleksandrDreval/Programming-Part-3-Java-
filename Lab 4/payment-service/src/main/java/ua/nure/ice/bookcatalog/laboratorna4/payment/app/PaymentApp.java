package ua.nure.ice.bookcatalog.laboratorna4.payment.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "ua.nure.ice.bookcatalog.laboratorna4.payment")
@EnableScheduling
@org.springframework.amqp.rabbit.annotation.EnableRabbit
@EntityScan(basePackages = "ua.nure.ice.bookcatalog.laboratorna4.payment")
@EnableJpaRepositories(basePackages = "ua.nure.ice.bookcatalog.laboratorna4.payment")
public class PaymentApp {
    public static void main(String[] args) {
        SpringApplication.run(PaymentApp.class, args);
    }
}
