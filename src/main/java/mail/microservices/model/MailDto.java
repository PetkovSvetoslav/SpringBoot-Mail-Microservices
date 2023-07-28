package mail.microservices.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MailDto {

    private String sender;
    private String recipient;
    private String subject;
    private String context;
    private String userId;


}