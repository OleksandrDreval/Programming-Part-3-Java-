package ua.nure.ice.bookcatalog.practical4.concurrency;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class Task2LifecycleTest {

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
        new Task2Lifecycle();
        Task2Lifecycle.main(new String[]{});

        String output = outContent.toString();
        assertTrue(output.contains("State after creation: NEW"));
    }

    @Test
    void testInterrupts() throws InterruptedException {
        Thread mainRunner = new Thread(() -> {
            try {
                Task2Lifecycle.main(new String[]{});
            } catch (Exception e) {}
        });
        mainRunner.start();
        Thread.sleep(50);
        
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        for (Thread t : threadSet) {
            if (t.getName().startsWith("Thread-") && t != Thread.currentThread() && t != mainRunner) {
                t.interrupt(); // interrupt the inventoryThread
            }
        }
        mainRunner.interrupt(); // interrupt sleep/join in main
        mainRunner.join();
    }
}
