package ua.nure.ice.bookcatalog.laboratorna1.repository;

import org.springframework.stereotype.Repository;
import ua.nure.ice.bookcatalog.laboratorna1.model.order.BookOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryOrderRepository implements OrderRepository {
    private final Map<String, BookOrder> storage = new ConcurrentHashMap<>();

    @Override
    public BookOrder save(BookOrder order) {
        storage.put(order.getOrderId(), order);
        return order;
    }

    @Override
    public Optional<BookOrder> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<BookOrder> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean deleteById(String id) {
        return storage.remove(id) != null;
    }
}
