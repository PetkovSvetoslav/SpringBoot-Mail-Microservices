package mail.microservices.service;

import mail.microservices.config.ApplicationBeanConfiguration;
import mail.microservices.model.AcceptedSender;
import mail.microservices.model.FileContent;
import mail.microservices.model.ReceivedMessage;
import mail.microservices.model.TransportEmailWrapper;
import mail.microservices.model.view.MailProviderView;

import mail.microservices.repository.AcceptedSenderRepository;
import mail.microservices.repository.FileContentRepository;
import mail.microservices.repository.ReceivedMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


@Service
public class EmailServiceImp implements EmailService{

    private final ApplicationBeanConfiguration emailSender;

    public EmailServiceImp(ApplicationBeanConfiguration emailSender, ReadAndWriteTypesServiceImpl readAndWriteService) {
        this.emailSender = emailSender;
        this.readAndWriteService = readAndWriteService;
    }


    @Override
    public void sendSimpleEmailMessage(String sender,String subject,String content,String recipient){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(recipient);
        message.setSubject(subject);
        message.setText(content);
        emailSender.getJavaMailSender().send(message);
    }

    @Override
    public void sendMimeMessageWithAttachment(TransportEmailWrapper transportWrapper) throws MessagingException {
        JavaMailSenderImpl mailProvider = createMailProvider(transportWrapper.getMailProviderView());
        Session session = Session.getDefaultInstance(mailProvider.getJavaMailProperties());
        try {

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(transportWrapper.getMailView().getFrom()));
            message.addRecipients(Message.RecipientType.TO, new InternetAddress[]{new InternetAddress(transportWrapper.getMailView().getRecipient())});
            message.setSubject(transportWrapper.getMailView().getSubject());

            BodyPart messageBodyPart1 = new MimeBodyPart();
            messageBodyPart1.setText(transportWrapper.getMailView().getContent());

            // create new MimeBodyPart object and set DataHandler object to this object
            MimeBodyPart messageBodyPart2 = new MimeBodyPart();

            // String filename = "SendAttachment.java";//change accordingly
            DataSource source = new FileDataSource(transportWrapper.getPath());
            messageBodyPart2.setDataHandler(new DataHandler(source));

            File file = new File(transportWrapper.getPath());
            String path = file.getName();
            messageBodyPart2.setFileName(path);

            // create Multipart object and add MimeBodyPart objects to this object
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart1);
            multipart.addBodyPart(messageBodyPart2);

            // set the multipart object to the message object
            message.setContent(multipart);

            // send message
            mailProvider.send(message);

        } catch (MessagingException ex) {
            ex.printStackTrace();
        }

//            MimeMessage message = mailProvider.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true);
//            helper.setSubject(transportWrapper.getMailView().getSubject());
//            helper.setFrom(transportWrapper.getMailView().getFrom());
//            helper.setTo(transportWrapper.getMailView().getRecipient());
//            // TODO replay to on RUN
//            helper.setText(transportWrapper.getMailView().getContent(), false);
//            helper.addAttachment(fileName, dataSource);
//            mailProvider.send(message);

    }

    @Override
    public JavaMailSenderImpl createMailProvider(MailProviderView mailProviderView) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailProviderView.getHost());
        mailSender.setPort(mailProviderView.getPort());
        mailSender.setUsername(mailProviderView.getUsername());
        mailSender.setPassword(mailProviderView.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

    //    NEW RECEIVE METHOD
    @Autowired
    private AcceptedSenderRepository acceptedSenderRepository;

    @Autowired
    private ReceivedMessageRepository receivedMessageRepository;

    @Autowired
    private FileContentRepository fileContentRepository;

    private static final Set<String> ACCEPTED_FILE_EXTENSIONS = new HashSet<>(
            Arrays.asList("xls", "xlsx", "csv")); // Add other accepted extensions here
    @Override
    public void receiveMail(Message[] messages) throws MessagingException {

        for (Message message : messages) {
            String senderEmail = ((InternetAddress) ((MimeMessage) message).getFrom()[0]).getAddress();

            AcceptedSender sender = acceptedSenderRepository.findBySenderEmail(senderEmail);
            if (sender != null && sender.isAccepted()) {
                try {
                    if (message.getContent() instanceof MimeMultipart) {
                        MimeMultipart multipart = (MimeMultipart) message.getContent();

                        ReceivedMessage receivedMessage = new ReceivedMessage();
                        receivedMessage.setSenderId(sender.getId());
                        receivedMessage.setReceivedDate(message.getReceivedDate());

                        for (int i = 0; i < multipart.getCount(); i++) {
                            MultipartFile file = (MultipartFile) multipart.getBodyPart(i).getContent();

                            String fileName = file.getOriginalFilename();
                            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);

                            if (ACCEPTED_FILE_EXTENSIONS.contains(fileExtension)) {
                                FileContent fileContent = new FileContent();
                                fileContent.setFileName(fileName);
                                fileContent.setFile(file);

                                // Save file and get saved path
                                // String savedPath = saveFile(file);
                                // fileContent.setFilePath(savedPath);

                                // save FileContent to DB
                                fileContentRepository.save(fileContent);

                                receivedMessage.getReceivedFiles().add(fileContent);
                            }
                        }

                        // save ReceivedMessage to DB
                        receivedMessageRepository.save(receivedMessage);
                    }
                } catch (MessagingException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

//    @Override
//    public void receiveMailwithFetch(MailProviderView mailProviderView, String[] senderEmails) throws MessagingException {
//
//    }

    @Override
    public List<Message> fetchMessagesFromSpecificSenders(MailProviderView mailProviderView, String[] senderEmails) throws MessagingException {
        List<Message> senderMessages = new ArrayList<>();
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");
        Session emailSession = Session.getDefaultInstance(properties);

        Store store = emailSession.getStore();
        store.connect(mailProviderView.getHost(), mailProviderView.getUsername(), mailProviderView.getPassword());

        Folder emailFolder = store.getFolder("INBOX");
        emailFolder.open(Folder.READ_ONLY);

        Message[] allMessages = emailFolder.getMessages();

        for (Message message : allMessages) {
            Address[] fromAddresses = message.getFrom();
            for (Address address : fromAddresses) {
                for (String senderEmail : senderEmails) {
                    if (address.toString().equalsIgnoreCase(senderEmail)) {
                        senderMessages.add(message);
                    }
                }
            }
        }
        return senderMessages;
    }

    @Override
    public Message[] fetchMessages(MailProviderView mailProviderView) throws MessagingException {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");
        Session emailSession = Session.getDefaultInstance(properties);

        Store store = emailSession.getStore();
        store.connect(mailProviderView.getHost(), mailProviderView.getUsername(), mailProviderView.getPassword());

        Folder emailFolder = store.getFolder("INBOX");
        emailFolder.open(Folder.READ_ONLY);

        return emailFolder.getMessages();
    }
//    This method should save the file to our server and return the saved path.
//    private String saveFile(MultipartFile file) {
//        return "/path/to/saved/file";
//    }

    @Override
    public void readMail(MailProviderView mailProviderView) throws MessagingException, IOException {
        Properties properties = new Properties();
        properties.put("mail.pop3s.host", mailProviderView.getHost());
        properties.put("mail.pop3s.port", mailProviderView.getPort());
        properties.put("mail.pop3s.starttls.enable", "true");

        Session emailSession = Session.getDefaultInstance(properties);
        Store store = emailSession.getStore("pop3s");

        store.connect(mailProviderView.getHost(), mailProviderView.getUsername(), mailProviderView.getPassword());

        Folder emailFolder = store.getFolder("INBOX");
        emailFolder.open(Folder.READ_ONLY);

        Message[] messages = emailFolder.getMessages();
        FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
        Message[] unreadMessages = emailFolder.search(ft);

        System.out.println("Number of unread messages : " + unreadMessages.length);

        for (Message message : unreadMessages) {
            System.out.println("---------------------------------");
            System.out.println("Email Number " + (message.getMessageNumber()));
            System.out.println("Subject: " + message.getSubject());
            System.out.println("From: " + message.getFrom()[0]);
            System.out.println("Text: " + message.getContent().toString());
        }

        emailFolder.close(false);
        store.close();
    }

    @Override
    public List<MultipartFile> getEmailAttachments(Message message) throws MessagingException, IOException {
        List<MultipartFile> attachments = new ArrayList<>();

        if (message.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) message.getContent();
            for (int i = 0; i < multipart.getCount(); i++) {
                Part part = multipart.getBodyPart(i);
                if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                    InputStream is = part.getInputStream();
                    MultipartFile multipartFile = new MockMultipartFile(part.getFileName(), part.getFileName(), part.getContentType(), is);
                    attachments.add(multipartFile);
                }
            }
        }
        return attachments;
    }



    @Override
    public Map<String, List<File>> reorderFilesBySenderEmail(List<Message> messages, Map<Message, List<File>> messageToFileMap) throws MessagingException {
        Map<String, List<File>> emailToFileMap = new HashMap<>();

        for (Message message : messages) {
            Address[] fromAddresses = message.getFrom();
            String fromEmail = ((InternetAddress) fromAddresses[0]).getAddress();
            if (!emailToFileMap.containsKey(fromEmail)) {
                emailToFileMap.put(fromEmail, new ArrayList<>());
            }

            List<File> files = messageToFileMap.get(message);
            emailToFileMap.get(fromEmail).addAll(files);
        }

        return emailToFileMap;
    }


    private final ReadAndWriteTypesServiceImpl readAndWriteService;


    @Override
    public void processFile(MultipartFile file, String routeId) {

        // Call the method to reorder the file by sender email
        File reorderedFile = reorderFileBySenderEmail(file);


        // Read result and wrap it to pnr.txt
        List<String> resultData = readAndWriteService.readDataFromCsv("path/to/pnr.txt");  // Replace the path accordingly
        Path pnrFilePath = Paths.get("path/to/pnr.txt");  // Replace the path accordingly

        // Convert list to string and write to pnr.txt
        String resultString = String.join(System.lineSeparator(), resultData);
        try {
            Files.writeString(pnrFilePath, resultString);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        // Send the result to the client
//        // This assumes you have a method in ReadAndWriteServiceImpl to send emails.
//        readAndWriteService.sendEmailWithAttachment(clientEmail, "Your Result", "Please find the attached pnr.txt", pnrFilePath);
    }

    private File reorderFileBySenderEmail(MultipartFile file) {
        // TODO: Implement the logic to reorder the file based on the sender's email.
        // The implementation depends on the structure of your file (e.g., JSON, CSV, etc).
        return null;
    }

    @Override
    public void sendProcessedFileToClient(MailProviderView mailProviderView, String to, String subject, String text, File processedFile) throws MessagingException {
        JavaMailSenderImpl javaMailSender = createMailProvider(mailProviderView);

        // Similar to the sendEmailWithAttachment method, but now we are sending the processed file
        MimeMessage message = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);

        FileSystemResource fileSystemResource = new FileSystemResource(processedFile);
        helper.addAttachment(fileSystemResource.getFilename(), fileSystemResource);

        javaMailSender.send(message);
    }



}
