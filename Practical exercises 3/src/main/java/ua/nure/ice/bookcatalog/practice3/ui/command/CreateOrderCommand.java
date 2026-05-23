package ua.nure.ice.bookcatalog.practice3.ui.command;

import ua.nure.ice.bookcatalog.practice3.messaging.EncryptionDecorator;
import ua.nure.ice.bookcatalog.practice3.messaging.LoggingDecorator;
import ua.nure.ice.bookcatalog.practice3.messaging.Message;
import ua.nure.ice.bookcatalog.practice3.messaging.SignatureDecorator;
import ua.nure.ice.bookcatalog.practice3.messaging.SimpleMessage;
import ua.nure.ice.bookcatalog.practice3.messaging.TimestampDecorator;
import ua.nure.ice.bookcatalog.practice3.model.Book;
import ua.nure.ice.bookcatalog.practice3.model.order.BookOrder;
import ua.nure.ice.bookcatalog.practice3.model.order.DeliveryAddress;
import ua.nure.ice.bookcatalog.practice3.service.CatalogService;
import ua.nure.ice.bookcatalog.practice3.ui.ConsoleInputHelper;

import java.util.List;

public class CreateOrderCommand implements MenuCommand {
  private final CatalogService catalogService;
  private final ConsoleInputHelper inputHelper;

  public CreateOrderCommand(CatalogService catalogService, ConsoleInputHelper inputHelper) {
    this.catalogService = catalogService;
    this.inputHelper = inputHelper;
  }

  @Override
  public void execute() {
    System.out.println("--- CREATE ORDER ---");
    try {
      String orderId = inputHelper.readNonEmptyText("Enter Order ID: ");
      String customerName = inputHelper.readNonEmptyText("Enter Customer Name: ");
      
      BookOrder.Builder orderBuilder = new BookOrder.Builder(orderId, customerName);
      
      System.out.println("\nAvailable books:");
      List<Book> books = catalogService.findAllBooks();
      if (books.isEmpty()) {
        System.out.println("Catalog is empty. Cannot create an order.");
        return;
      }
      for (Book book : books) {
        System.out.println(book.getId() + " - " + book.getTitle() + " by " + book.getAuthor());
      }
      
      while (true) {
        long bookId = inputHelper.readLong("Enter Book ID to add (or 0 to finish): ");
        if (bookId == 0) {
          break;
        }
        Book selectedBook = books.stream().filter(b -> b.getId() == bookId).findFirst().orElse(null);
        if (selectedBook != null) {
          orderBuilder.addBook(selectedBook);
          System.out.println("Added: " + selectedBook.getTitle());
        } else {
          System.out.println("Book with ID " + bookId + " not found.");
        }
      }

      boolean withAddress = inputHelper.readBoolean("\nDo you want to specify a delivery address? (y/n): ");
      if (withAddress) {
        String country = inputHelper.readNonEmptyText("Country: ");
        String city = inputHelper.readNonEmptyText("City: ");
        String street = inputHelper.readNonEmptyText("Street: ");
        String building = inputHelper.readNonEmptyText("Building: ");
        
        DeliveryAddress.Builder addressBuilder = new DeliveryAddress.Builder(country, city, street, building);
        
        String apartment = inputHelper.readOptionalText("Apartment (optional): ");
        if (apartment != null) {
          addressBuilder.apartmentNumber(apartment);
        }
        
        String postalCode = inputHelper.readOptionalText("Postal Code (optional): ");
        if (postalCode != null) {
          addressBuilder.postalCode(postalCode);
        }
        
        String details = inputHelper.readOptionalText("Additional Details (optional): ");
        if (details != null) {
          addressBuilder.additionalDetails(details);
        }
        
        orderBuilder.deliveryAddress(addressBuilder.build());
      }

      boolean urgent = inputHelper.readBoolean("Is the order urgent? (y/n): ");
      if (urgent) {
        orderBuilder.isUrgent(true);
      }
      
      String paymentMethod = inputHelper.readOptionalText("Payment Method (optional): ");
      if (paymentMethod != null) {
        orderBuilder.paymentMethod(paymentMethod);
      }
      
      String comment = inputHelper.readOptionalText("Comment (optional): ");
      if (comment != null) {
        orderBuilder.orderComment(comment);
      }

      BookOrder order = orderBuilder.build();
      System.out.println("\n--- Order Successfully Created ---");
      System.out.println(order);
      
      boolean sendNotification = inputHelper.readBoolean("\nDo you want to send a notification about the order? (y/n): ");
      if (sendNotification) {
        Message message = new SimpleMessage("Order " + order.getOrderId() + " has been successfully created!");
        
        while (true) {
          System.out.println("\nSelect decorators to apply (or 0 to finish):");
          System.out.println("1. Log");
          System.out.println("2. Timestamp");
          System.out.println("3. Encrypt");
          System.out.println("4. Sign");
          
          int choice = inputHelper.readInt("Your choice: ");
          if (choice == 0) {
            break;
          }
          
          switch (choice) {
            case 1:
              message = new LoggingDecorator(message);
              break;
            case 2:
              message = new TimestampDecorator(message);
              break;
            case 3:
              message = new EncryptionDecorator(message);
              break;
            case 4:
              message = new SignatureDecorator(message);
              break;
            default:
              System.out.println("Invalid choice. Try again.");
          }
        }
        
        System.out.println("\nFinal message:");
        System.out.println(message.getContent());
      }
      
    } catch (IllegalArgumentException e) {
      System.out.println("Error creating order: " + e.getMessage());
    }
  }

  @Override
  public String getTitle() {
    return "Create new order interactively (Builder & Decorator)";
  }
}
