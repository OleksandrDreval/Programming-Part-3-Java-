package ua.nure.ice.bookcatalog.laboratorna4.repository;

import ua.nure.ice.bookcatalog.laboratorna4.model.order.BookOrder;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<BookOrder, Long> {
    boolean existsByBooks_Id(Long bookId);
}
