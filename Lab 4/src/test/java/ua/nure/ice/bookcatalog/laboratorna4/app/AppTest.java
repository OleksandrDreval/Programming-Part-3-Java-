package ua.nure.ice.bookcatalog.laboratorna4.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class AppTest {

  @Test
  void contextLoads() {
    // Verifies that the Spring application context loads successfully
  }
  
  @Test
  void mainMethodShouldRunSuccessfully() {
      // Testing the main method by passing a spring profile argument or port 0
      assertDoesNotThrow(() -> App.main(new String[]{"--server.port=0"}));
  }
}
