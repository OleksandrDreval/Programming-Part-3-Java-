package ua.nure.ice.bookcatalog.laboratorna3.repository;

import ua.nure.ice.bookcatalog.laboratorna3.model.order.BookOrder;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    BookOrder save(BookOrder order);
    Optional<BookOrder> findById(String id);
    List<BookOrder> findAll();
    boolean deleteById(String id);
}
