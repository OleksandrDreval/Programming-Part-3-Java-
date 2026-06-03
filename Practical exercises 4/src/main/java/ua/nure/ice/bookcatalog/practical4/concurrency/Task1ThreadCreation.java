package ua.nure.ice.bookcatalog.practical4.concurrency;

public class Task1ThreadCreation {
    public static void main(String[] args) {
        System.out.println("Starting Task 1: Thread Creation");

        Thread importThread = new BookImportThread();

        Thread exportThread = new Thread(new BookExportRunnable(), "BookExportThread");

        importThread.start();
        exportThread.start();
    }
}

class BookImportThread extends Thread {
    public BookImportThread() {
        super("BookImportThread");
    }

    @Override
    public void run() {
        for (int i = 1; i <= 5; i++) {
            System.out.println(getName() + " importing book batch " + i);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }
}

class BookExportRunnable implements Runnable {
    @Override
    public void run() {
        for (int i = 1; i <= 5; i++) {
            System.out.println(Thread.currentThread().getName() + " exporting book batch " + i);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }
}
