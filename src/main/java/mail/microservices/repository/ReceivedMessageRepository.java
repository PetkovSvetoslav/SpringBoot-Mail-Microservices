package mail.microservices.repository;

import mail.microservices.model.ReceivedMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceivedMessageRepository extends JpaRepository<ReceivedMessage, Long> {
}