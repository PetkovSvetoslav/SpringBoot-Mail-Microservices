package mail.microservices.model;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
public class ReceivedMessage {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private Long senderId;
    private Date receivedDate;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<FileContent> receivedFiles;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public Date getReceivedDate() { return receivedDate; }
    public void setReceivedDate(Date receivedDate) { this.receivedDate = receivedDate; }

    public Set<FileContent> getReceivedFiles() { return receivedFiles; }
    public void setReceivedFiles(Set<FileContent> receivedFiles) { this.receivedFiles = receivedFiles; }
}