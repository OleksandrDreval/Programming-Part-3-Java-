package ua.nure.ice.bookcatalog.practical4.concurrency;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class Task3JoinTest {

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
        new Task3Join();
        Task3Join.main(new String[]{});
        String output = outContent.toString();
        assertTrue(output.contains("All threads finished"));
    }

    @Test
    void testInterrupts() throws InterruptedException {
        Thread mainRunner = new Thread(() -> {
            try {
                Task3Join.main(new String[]{});
            } catch (Exception e) {}
        });
        mainRunner.start();
        Thread.sleep(50);
        
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        for (Thread t : threadSet) {
            if (t.getName().startsWith("Thread-") && t != Thread.currentThread() && t != mainRunner) {
                t.interrupt();
            }
        }
        mainRunner.interrupt();
        mainRunner.join();
    }
}
