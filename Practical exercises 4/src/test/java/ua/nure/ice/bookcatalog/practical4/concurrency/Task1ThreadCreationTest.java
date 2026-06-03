package ua.nure.ice.bookcatalog.practical4.concurrency;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class Task1ThreadCreationTest {

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
        new Task1ThreadCreation(); // cover default constructor
        Task1ThreadCreation.main(new String[]{});
        Thread.sleep(1600); // wait enough time
        String output = outContent.toString();
        assertTrue(output.contains("Starting Task 1: Thread Creation"));
    }

    @Test
    void testInterrupts() throws InterruptedException {
        BookImportThread t1 = new BookImportThread();
        t1.start();
        t1.interrupt();
        t1.join();

        Thread t2 = new Thread(new BookExportRunnable());
        t2.start();
        t2.interrupt();
        t2.join();
    }
}
