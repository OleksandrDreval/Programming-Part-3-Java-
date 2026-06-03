package ua.nure.ice.bookcatalog.practical4.concurrency;

public class Task4RaceCondition {
    public static void main(String[] args) {
        System.out.println("Starting Task 4: Race Condition");

        BookRentalOffice office = new BookRentalOffice();

        Thread[] customers = new Thread[12];

        for (int i = 0; i < customers.length; i++) {
            customers[i] = new Thread(() -> {
                office.rentBook(); 
            }, "Customer-" + (i + 1));
        }

        for (Thread customer : customers) {
            customer.start();
        }

        for (Thread customer : customers) {
            try {
                customer.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Final available copies: " + office.getAvailableCopies());

    }
}
