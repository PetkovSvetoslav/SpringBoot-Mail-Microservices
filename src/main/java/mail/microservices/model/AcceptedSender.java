package mail.microservices.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class AcceptedSender {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String senderDomain;
    private String senderEmail;
    private String senderName;
    private boolean accepted;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSenderDomain() { return senderDomain; }
    public void setSenderDomain(String senderDomain) { this.senderDomain = senderDomain; }

    public String getSenderEmail() { return senderEmail; }
    public void setSenderEmail(String senderEmail) { this.senderEmail = senderEmail; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public boolean isAccepted() { return accepted; }
    public void setAccepted(boolean accepted) { this.accepted = accepted; }
}

