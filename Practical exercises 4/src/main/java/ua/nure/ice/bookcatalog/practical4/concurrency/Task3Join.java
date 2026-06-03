package ua.nure.ice.bookcatalog.practical4.concurrency;

public class Task3Join {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting Task 3: Thread Join");
        System.out.println("Main thread started generating reports...");

        Thread pdfReportThread = new Thread(() -> {
            System.out.println("PDF Report Thread: Generating PDF...");
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("PDF Report Thread: Finished PDF generation.");
        });

        Thread csvReportThread = new Thread(() -> {
            System.out.println("CSV Report Thread: Generating CSV...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("CSV Report Thread: Finished CSV generation.");
        });

        pdfReportThread.start();
        csvReportThread.start();

        pdfReportThread.join();
        csvReportThread.join();

        System.out.println("All threads finished");
    }
}
