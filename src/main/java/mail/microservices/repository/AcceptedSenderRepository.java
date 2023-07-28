package mail.microservices.repository;

import mail.microservices.model.AcceptedSender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AcceptedSenderRepository extends JpaRepository<AcceptedSender, Long> {
    AcceptedSender findBySenderEmail(String senderEmail);
}
