package mail.microservices.model.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReceiveMailView {

    private String recipientName;
    private String senderName;
    private String content;
    private String subject;
    private List<String> attachmentFileName;
}
