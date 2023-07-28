package mail.microservices.model.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileInfoView {

   private String path;
   private String ownerId;
   private String fileName;
   private byte[] bytes;
}
