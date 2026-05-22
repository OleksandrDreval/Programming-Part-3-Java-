package ua.nure.ice.bookcatalog.practice3.app;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AppTest {
  private final InputStream originalIn = System.in;
  private final PrintStream originalOut = System.out;
  private ByteArrayOutputStream outputStream;

  @BeforeEach
  void setUp() {
    outputStream = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStream));
  }

  @AfterEach
  void tearDown() {
    System.setIn(originalIn);
    System.setOut(originalOut);
  }

  @Test
  void mainShouldStartUiAndExitGracefully() {
    System.setIn(new ByteArrayInputStream("0\n".getBytes(StandardCharsets.UTF_8)));

    App.main(new String[0]);

    String output = outputStream.toString(StandardCharsets.UTF_8);
    assertTrue(output.contains("Welcome to Book Catalog"));
    assertTrue(output.contains("Goodbye."));
  }
}
