package mail.microservices.model;


import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;

@Entity
public class FileContent {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String fileName;
    private String filePath;

    // We don't persist the MultipartFile object, only information about the file
    @Transient
    private MultipartFile file;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public MultipartFile getFile() { return file; }
    public void setFile(MultipartFile file) { this.file = file; }
}