package mail.microservices.service;

import org.springframework.web.multipart.MultipartFile;

import javax.mail.Message;

public interface ParserService {
    void csvParser(MultipartFile file, Message massage);

    void parseXls(Message message);

    void parseXlsx(Message message);
}
