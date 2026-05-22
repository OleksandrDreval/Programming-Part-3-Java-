package ua.nure.ice.bookcatalog.practice3.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import org.junit.jupiter.api.Test;
import ua.nure.ice.bookcatalog.practice3.model.BookGenre;

class ConsoleInputHelperTest {

  @Test
  void readNonEmptyTextShouldTrimValue() {
    ConsoleInputHelper inputHelper = createInputHelper("   Dune   \n");

    String value = inputHelper.readNonEmptyText("Prompt: ");

    assertEquals("Dune", value);
  }

  @Test
  void readNonEmptyTextShouldThrowWhenBlank() {
    ConsoleInputHelper inputHelper = createInputHelper("   \n");

    assertThrows(IllegalArgumentException.class,
        () -> inputHelper.readNonEmptyText("Prompt: "));
  }

  @Test
  void readIntShouldParseValue() {
    ConsoleInputHelper inputHelper = createInputHelper("42\n");

    int value = inputHelper.readInt("Prompt: ");

    assertEquals(42, value);
  }

  @Test
  void readIntShouldThrowWhenInvalid() {
    ConsoleInputHelper inputHelper = createInputHelper("abc\n");

    assertThrows(IllegalArgumentException.class,
        () -> inputHelper.readInt("Prompt: "));
  }

  @Test
  void readLongShouldParseValue() {
    ConsoleInputHelper inputHelper = createInputHelper("123456789\n");

    long value = inputHelper.readLong("Prompt: ");

    assertEquals(123456789L, value);
  }

  @Test
  void readLongShouldThrowWhenInvalid() {
    ConsoleInputHelper inputHelper = createInputHelper("abc\n");

    assertThrows(IllegalArgumentException.class,
        () -> inputHelper.readLong("Prompt: "));
  }

  @Test
  void readGenreShouldReturnGenreByIndex() {
    ConsoleInputHelper inputHelper = createInputHelper("1\n");

    BookGenre genre = inputHelper.readGenre();

    assertEquals(BookGenre.FICTION, genre);
  }

  @Test
  void readGenreShouldThrowWhenIndexOutOfRange() {
    ConsoleInputHelper inputHelper = createInputHelper("99\n");

    assertThrows(IllegalArgumentException.class, inputHelper::readGenre);
  }

  private ConsoleInputHelper createInputHelper(String input) {
    ByteArrayInputStream inputStream =
        new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
    return new ConsoleInputHelper(new Scanner(inputStream));
  }
}
