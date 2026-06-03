package ua.nure.ice.bookcatalog.practical4.concurrency;

public class Task2Lifecycle {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting Task 2: Thread Lifecycle");

        Thread inventoryThread = new Thread(() -> {
            System.out.println("Inventory check started...");
            try {

                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
            System.out.println("Inventory check finished.");
        });

        System.out.println("State after creation: " + inventoryThread.getState()); 

        inventoryThread.start();
        System.out.println("State after start(): " + inventoryThread.getState()); 

        Thread.sleep(500); 
        System.out.println("State during execution (sleep): " + inventoryThread.getState()); 

        inventoryThread.join(); 
        System.out.println("State after completion: " + inventoryThread.getState()); 
    }
}
