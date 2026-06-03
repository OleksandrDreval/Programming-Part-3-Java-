package ua.nure.ice.bookcatalog.practical4.concurrency;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class Task4RaceConditionTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    void testMainMethodOutput() {
        new Task4RaceCondition();
        Task4RaceCondition.main(new String[]{});

        String output = outContent.toString();
        assertTrue(output.contains("Final available copies: "));
    }

    @Test
    void testInterrupts() throws InterruptedException {
        Thread mainRunner = new Thread(() -> {
            Task4RaceCondition.main(new String[]{});
        });
        mainRunner.start();
        Thread.sleep(50);
        mainRunner.interrupt(); // hits catch (InterruptedException e) for customer.join()
        mainRunner.join();
    }
}
