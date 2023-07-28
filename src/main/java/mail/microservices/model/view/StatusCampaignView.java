package mail.microservices.model.view;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StatusCampaignView {

    private String recipientAddress;
    private int statusCode;
    private String statusDescription;
    private String dateAndTime;
}
