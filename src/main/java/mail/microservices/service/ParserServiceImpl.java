package mail.microservices.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class ParserServiceImpl implements ParserService{
    @Override
    public void csvParser(MultipartFile file, Message message) {
        try {
            if (message.getContent() instanceof MimeMultipart) {
                MimeMultipart multipart = (MimeMultipart) message.getContent();
                for (int i = 0; i < multipart.getCount(); i++) {
                    if (multipart.getBodyPart(i).isMimeType("text/csv")) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(multipart.getBodyPart(i).getInputStream()));
                        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader());
                        List<CSVRecord> csvRecords = csvParser.getRecords();

                        for (CSVRecord record : csvRecords) {
                            String columnOneValue = record.get("ColumnOne"); // replace "ColumnOne" with  actual column name
                            String columnTwoValue = record.get("ColumnTwo"); // replace "ColumnTwo" with  actual column name
                            // do something with the values
                        }

                        csvParser.close();
                    }
                }
            }
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void parseXls(Message message) {
        try {
            if (message.getContent() instanceof MimeMultipart) {
                MimeMultipart multipart = (MimeMultipart) message.getContent();
                for (int i = 0; i < multipart.getCount(); i++) {
                    String contentType = multipart.getBodyPart(i).getContentType();
                    if (contentType.equals("application/vnd.ms-excel")) { // .xls
                        try (InputStream is = multipart.getBodyPart(i).getInputStream();
                             Workbook workbook = new HSSFWorkbook(is)) {
                            parseWorkbook(workbook);
                        }
                    }
                }
            }
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void parseXlsx(Message message) {
        try {
            if (message.getContent() instanceof MimeMultipart) {
                MimeMultipart multipart = (MimeMultipart) message.getContent();
                for (int i = 0; i < multipart.getCount(); i++) {
                    String contentType = multipart.getBodyPart(i).getContentType();
                    if (contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) { // .xlsx
                        try (InputStream is = multipart.getBodyPart(i).getInputStream();
                             Workbook workbook = new XSSFWorkbook(is)) {
                            parseWorkbook(workbook);
                        }
                    }
                }
            }
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
    }

    private void parseWorkbook(Workbook workbook) {
        Sheet sheet = workbook.getSheetAt(0); // getting the first sheet
        for (Row row : sheet) {
            for (Cell cell : row) {
                String cellValue = new DataFormatter().formatCellValue(cell); // getting cell value
                // handle the value here
            }
        }
    }

}
