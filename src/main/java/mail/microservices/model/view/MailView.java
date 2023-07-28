package mail.microservices.model.view;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MailView {
    private String from;
    private String content;
    private String recipient;
    private String subject;
}
