package mail.microservices.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mail.microservices.model.view.FileInfoView;
import mail.microservices.model.view.ReceiveMailView;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WebClientWrapper {

    private List<FileInfoView> filePathView;

    private List<ReceiveMailView> receiveMailViews;
}
