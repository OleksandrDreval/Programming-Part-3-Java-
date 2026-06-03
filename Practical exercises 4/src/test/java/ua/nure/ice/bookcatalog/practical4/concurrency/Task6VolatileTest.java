package ua.nure.ice.bookcatalog.practical4.concurrency;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class Task6VolatileTest {

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
    void testMainMethodOutput() throws InterruptedException {
        new Task6Volatile();
        Task6Volatile.main(new String[]{});

        String output = outContent.toString();
        assertTrue(output.contains("Starting Task 6: Volatile"));
    }

    @Test
    void testInterrupts() throws InterruptedException {
        BackgroundLibraryCleaner cleaner = new BackgroundLibraryCleaner();
        Thread cleanerThread = new Thread(cleaner);
        cleanerThread.start();
        Thread.sleep(100);
        cleanerThread.interrupt(); // hit catch block inside BackgroundLibraryCleaner
        cleaner.stopRunning();
        cleanerThread.join();
    }
}
