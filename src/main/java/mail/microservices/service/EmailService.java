package mail.microservices.service;

import mail.microservices.model.TransportEmailWrapper;
import mail.microservices.model.view.MailProviderView;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.multipart.MultipartFile;
import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface EmailService {
//     void sendSimpleEmailMessage(TransportEmailWrapper transportEmailWrapper) throws MessagingException;

    void sendSimpleEmailMessage(String sender, String subject, String content, String recipient);

    void sendMimeMessageWithAttachment(TransportEmailWrapper transportWrapper) throws MessagingException;

      JavaMailSenderImpl createMailProvider(MailProviderView mailProviderView);

void receiveMail(Message[] messages) throws MessagingException;

    List<Message> fetchMessagesFromSpecificSenders(MailProviderView mailProviderView, String[] senderEmails) throws MessagingException;

    Message[] fetchMessages(MailProviderView mailProviderView) throws MessagingException;

    void readMail(MailProviderView mailProviderView) throws MessagingException, IOException;

     List<MultipartFile> getEmailAttachments(Message message) throws MessagingException, IOException;

    Map<String, List<File>> reorderFilesBySenderEmail(List<Message> messages, Map<Message, List<File>> messageToFileMap) throws MessagingException;

//    void processFile(MultipartFile file, String routeId);

    void processFile(MultipartFile file, String routeId);

    void sendProcessedFileToClient(MailProviderView mailProviderView, String to, String subject, String text, File processedFile) throws MessagingException;


}
