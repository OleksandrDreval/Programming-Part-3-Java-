package ua.nure.ice.bookcatalog.practical4.concurrency;

public class BookRentalOffice {
    private int availableCopies = 10;

    public void rentBook() {
        if (availableCopies > 0) {
            try {

                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
            availableCopies--;
            System.out.println(Thread.currentThread().getName() + " rented a book. Available: " + availableCopies);
        } else {
            System.out.println(Thread.currentThread().getName() + " tried to rent a book, but no copies available.");
        }
    }

    public synchronized void rentBookSynchronized() {
        if (availableCopies > 0) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
            availableCopies--;
            System.out.println(Thread.currentThread().getName() + " rented a book. Available: " + availableCopies);
        } else {
            System.out.println(Thread.currentThread().getName() + " tried to rent a book, but no copies available.");
        }
    }

    public int getAvailableCopies() {
        return availableCopies;
    }
}
