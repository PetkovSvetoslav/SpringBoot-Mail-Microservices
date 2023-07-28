package mail.microservices.model.view;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MailProviderView {

    private String host;
    private int port;
    private String username;
    private String password;
}
