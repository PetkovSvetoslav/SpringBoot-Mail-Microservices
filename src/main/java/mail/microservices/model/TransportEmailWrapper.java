package mail.microservices.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mail.microservices.model.view.MailProviderView;
import mail.microservices.model.view.MailView;

@Getter
@Setter
@NoArgsConstructor
public class TransportEmailWrapper {
    private MailProviderView mailProviderView;
    private MailView mailView;
    private String path;
}
