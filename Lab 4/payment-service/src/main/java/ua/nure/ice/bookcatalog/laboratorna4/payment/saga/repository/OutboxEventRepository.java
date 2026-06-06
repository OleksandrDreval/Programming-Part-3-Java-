package ua.nure.ice.bookcatalog.laboratorna4.payment.saga.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.nure.ice.bookcatalog.laboratorna4.payment.saga.model.OutboxEvent;
import ua.nure.ice.bookcatalog.laboratorna4.payment.saga.model.OutboxEventStatus;

import java.util.List;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent> findTop20ByStatusOrderByCreatedAtAsc(OutboxEventStatus status);
}
