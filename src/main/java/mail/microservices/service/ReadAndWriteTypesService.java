package mail.microservices.service;

import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public interface ReadAndWriteTypesService {

    void writeToFile(String directoryName, String filename, String content) throws IOException;
    void writeListToFile(String directoryName, String filename, List<String> content) throws IOException;
    String checkFileName(String fileName);
    void checkFileSize(File file);
    List<String> readPnlData(String fileNameAndPath,int firstLine);
    List<String> readDataFromCsv(String fileNameAndPath);
    List<String> readLastLine(String fileName, Integer numberOfLines);
    Map<Integer, List<String>> readXlsx(File file, int firstRecordNumber);
    Path writeToFileSystem(String directory, MultipartFile file, String fileName);
    boolean writeToExcel(Map<String, Object[]> source, String directoryName,String nameTable) throws IOException;
    void writeXml(Document doc, OutputStream output) throws TransformerException, UnsupportedEncodingException;
    void copyFile(String originalPathSource, String destinationPath);
    // flightId is set for future check for persisted entries in DB
    Map<Integer,List<String>> readPassengersFromXlsx(Path fileNameAndPath, Long flightId, Integer rowsToRead,Integer firstRowToRead);
}
