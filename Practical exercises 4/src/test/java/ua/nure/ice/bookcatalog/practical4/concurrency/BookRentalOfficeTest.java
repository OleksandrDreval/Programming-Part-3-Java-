package ua.nure.ice.bookcatalog.practical4.concurrency;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BookRentalOfficeTest {

    @Test
    void testRentBook() {
        BookRentalOffice office = new BookRentalOffice();
        office.rentBook();
        assertEquals(9, office.getAvailableCopies());
        for (int i = 0; i < 9; i++) {
            office.rentBook();
        }
        office.rentBook(); // out of copies
        assertEquals(0, office.getAvailableCopies());
    }

    @Test
    void testRentBookSynchronized() {
        BookRentalOffice office = new BookRentalOffice();
        office.rentBookSynchronized();
        assertEquals(9, office.getAvailableCopies());
        for (int i = 0; i < 9; i++) {
            office.rentBookSynchronized();
        }
        office.rentBookSynchronized(); // out of copies
        assertEquals(0, office.getAvailableCopies());
    }

    @Test
    void testRentBookInterrupt() throws InterruptedException {
        BookRentalOffice office = new BookRentalOffice();
        Thread t = new Thread(office::rentBook);
        t.start();
        t.interrupt();
        t.join();

        Thread t2 = new Thread(office::rentBookSynchronized);
        t2.start();
        t2.interrupt();
        t2.join();
    }
}
