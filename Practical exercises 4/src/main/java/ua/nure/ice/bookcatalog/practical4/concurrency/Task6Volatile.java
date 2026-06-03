package ua.nure.ice.bookcatalog.practical4.concurrency;

public class Task6Volatile {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting Task 6: Volatile");

        BackgroundLibraryCleaner cleaner = new BackgroundLibraryCleaner();
        Thread cleanerThread = new Thread(cleaner, "CleanerThread");

        cleanerThread.start();

        Thread.sleep(2000);

        System.out.println("Main thread is stopping the cleaner...");
        cleaner.stopRunning();

        cleanerThread.join();
        System.out.println("Main thread finished.");
    }
}

class BackgroundLibraryCleaner implements Runnable {

    private volatile boolean running = true;

    public void stopRunning() {
        this.running = false;
    }

    @Override
    public void run() {
        while (running) {
            System.out.println(Thread.currentThread().getName() + " is running background tasks...");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
        System.out.println(Thread.currentThread().getName() + " stopped.");
    }
}
