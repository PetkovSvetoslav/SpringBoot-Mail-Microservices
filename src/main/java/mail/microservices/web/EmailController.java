package mail.microservices.web;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import mail.microservices.model.MailDto;
import mail.microservices.model.TransportEmailWrapper;
import mail.microservices.model.view.MailProviderView;

import mail.microservices.service.EmailService;

import org.springframework.web.bind.annotation.*;


import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;

@RestController
public class EmailController {

    private final EmailService emailService;
    private final JsonObject jsonObject;
    private final Gson gson;

    public EmailController(EmailService emailService, JsonObject jsonObject, Gson gson) {
        this.emailService = emailService;
        this.jsonObject = jsonObject;
        this.gson = gson;
    }

    //    @PreAuthorize("hasAnyRole('ROLE_AIRLINE','ROLE_AGENT','ROLE_ROOT')")
    @PostMapping("/sendMail")
    public @ResponseBody
    String sendMail(MailDto mailDto) {

        // TODO mail obj impl and return back status.
        emailService.sendSimpleEmailMessage("support@intellisoft.guru", mailDto.getSubject(), mailDto.getContext(), mailDto.getRecipient());
        jsonObject.addProperty("message", " mail was send successfully! We will contact you ASAP!");
        return gson.toJson(jsonObject);
    }

    @PostMapping("/receive-mail")
    public void receiveMailMessage() throws MessagingException, IOException {
        MailProviderView mailProviderView1 = new MailProviderView();
        mailProviderView1.setHost("164.138.221.218");
        mailProviderView1.setUsername("info@intellisoft.guru");
        mailProviderView1.setPassword("Wqzhi92!Sk1o0m!QHg5z0m@!3D90!%ETQbAiMkuLloDGg1");
        mailProviderView1.setPort(465);

        Message[] messages = emailService.fetchMessages(mailProviderView1);
        emailService.receiveMail(messages);
    }


    @PostMapping("/send-email-with-attachment")
    public void sendSimpleEmailMessageWithAttachment(@RequestBody TransportEmailWrapper transportEmailWrapper) throws MessagingException {
//        MailProviderView mailProviderView1 = new MailProviderView();
//        mailProviderView1.setHost("164.138.221.218");
//        mailProviderView1.setUsername("info@intellisoft.guru");
//        mailProviderView1.setPassword("Wqzhi92!Sk1o0m!QHg5z0m@!3D90!%ETQbAiMkuLloDGg1");
//        mailProviderView1.setPort(465);
//
//        MailView mailView = new MailView();
//        mailView.setFrom("info@intellisoft.guru");
//        mailView.setRecipient("lor4eto111@gmail.com");
//        mailView.setSubject("Test Email");
//        mailView.setContent("This is a test email");
//
//        TransportEmailWrapper transportEmailWrapper = new TransportEmailWrapper();
//        transportEmailWrapper.setMailProviderView(mailProviderView1);
//        transportEmailWrapper.setMailView(mailView);

        emailService.sendMimeMessageWithAttachment(transportEmailWrapper);
    }


}



