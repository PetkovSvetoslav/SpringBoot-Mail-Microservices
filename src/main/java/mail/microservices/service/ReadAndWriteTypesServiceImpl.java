package mail.microservices.service;

import lombok.SneakyThrows;
import mail.microservices.config.GetPathBySystem;
import mail.microservices.config.LoggerHandler;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import org.w3c.dom.Document;

//import javax.swing.text.Document;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class ReadAndWriteTypesServiceImpl implements ReadAndWriteTypesService{


    private static final File TEMP_DIRECTORY = new File(System.getProperty("java.io.tmpdir"));
    private final LoggerHandler loggerHandler;

    @Autowired
    private GetPathBySystem getPathBySystem;

    public ReadAndWriteTypesServiceImpl(LoggerHandler loggerHandler) {
        this.loggerHandler = loggerHandler;
    }


    @Override
    public void writeToFile(String directoryName, String filename, String content) throws IOException {


        File logDirectory = new File(TEMP_DIRECTORY, directoryName);
        if (!logDirectory.exists()) {
            //noinspection ResultOfMethodCallIgnored
            logDirectory.mkdir();
        }
        String fileNameAndPath = logDirectory + directoryName + ".txt";

        BufferedWriter bw = new BufferedWriter(new FileWriter(fileNameAndPath, true));

        bw.write(content);
        bw.newLine();
        bw.close();

    }

    @Override
    public void writeListToFile(String directoryName, String filename, List<String> content) throws IOException {

        try {
            String pathToFile = getPathBySystem.getPathBySystem(directoryName);
            String fileNameAndPath = pathToFile + File.separator + filename;

            BufferedWriter bw = new BufferedWriter(new FileWriter(fileNameAndPath, true));
            for (String s : content) {
                bw.write(s);
                bw.write("\n");
            }

            bw.close();

        } catch (IOException e) {
            loggerHandler.loggerHandler("eu.pnlpnr.ems.service.fileManipulation",e.getMessage(),"WARNING","error");
            loggerHandler.loggerHandler("eu.pnlpnr.ems.service.fileManipulation","Writing file with file name: "+ filename + "not finished!","WARNING","error");
        }

    }

    @Override
    public String checkFileName(String fileName) {
        if (fileName.equals("USER_LOG")) {
            return "USER_LOG";
        }
        return "QUERY_LOG";
    }

    @Override
    public void checkFileSize(File file) {
        if (file.length() >= 15000) {
            int calendar = Calendar.DATE;
            File newFile = new File(TEMP_DIRECTORY, file.getAbsolutePath() + calendar);

            Path source = Paths.get(file.getAbsolutePath());
            Path target = Paths.get(newFile.getAbsolutePath());

            try {

                Files.move(source, target);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public List<String> readPnlData(String fileNameAndPath,int firstLine) {
        List<String> cellData = new ArrayList();
        List<String> rowData = new ArrayList();


        try {
            FileInputStream excelFile = new FileInputStream(fileNameAndPath);
            XSSFWorkbook wb = new XSSFWorkbook(excelFile);
            Sheet sheet = wb.getSheetAt(0);
            Integer endRow = sheet.getLastRowNum();

            Row row;
            int counter = 0;

            for (int i = firstLine; i <= endRow; i++) {
                counter = 0;
                //row.getFirstCellNum()
                row = sheet.getRow(i);
                for (int j = 0; j < 30; j++) {
                    Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    if (counter > row.getLastCellNum()) {
                        counter = row.getLastCellNum();
                    }

                    switch (cell.getCellType()) {
                        case STRING:
                            cellData.add(counter, cell.getStringCellValue().toUpperCase());
                            break;
                        case NUMERIC:
                            Double cellValue = cell.getNumericCellValue();
                            Integer cellValuewInt = cellValue.intValue();
                            String cellValueText = cellValuewInt.toString();
                            cellData.add(counter, cellValueText);
                            break;
                        case BLANK:
                            cellData.add(counter, "$");
                            break;
                        default:
                            cellData.add(counter, "$");

                    }
                    counter++;
                }

                ;
                rowData.add(String.join(",", cellData));
                cellData.clear();
                excelFile.close();
                //  Files.deleteIfExists(fileNameAndPath);
                wb.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowData;
    }

    @SneakyThrows
    @Override
    public List<String> readDataFromCsv(String fileNameAndPath) {
        List<String> csvInputList = new ArrayList<>();
        try {
            FileReader fr=new FileReader(fileNameAndPath);
            BufferedReader br=new BufferedReader(fr);

            String line="";
            while((line=br.readLine())!=null){
                csvInputList.add(line);
            }
            br.close();
            fr.close();

            return csvInputList;

        }catch (IOException e) {
            loggerHandler.loggerHandler("eu.pnlpnr.ems.service.fileManipulation",e.getMessage(),"WARNING","error");
            loggerHandler.loggerHandler("eu.pnlpnr.ems.service.fileManipulation","File "+fileNameAndPath +" not found!","WARNING","error");
        }
        return null;
    }

    @Override
    public List<String> readLastLine(String fileName, Integer numberOfLines) {
        List<String> result = new ArrayList<>();
        File fileDirectory = new File(getPathBySystem.getPathBySystem("log"));
        File file = new File(fileDirectory + File.separator + fileName + ".log");

        try (ReversedLinesFileReader reader = new ReversedLinesFileReader(file, StandardCharsets.UTF_8)) {

            String line;
            while ((line = reader.readLine()) != null && result.size() < numberOfLines) {
                result.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public Map<Integer, List<String>> readXlsx(File file, int firstRecordNumber) {
        Map<Integer, List<String>> sourceMap = new HashMap<>();
        List<String> cellData = new ArrayList<>();
        Row row;
        try{

            FileInputStream fis = new FileInputStream(file);
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet sheet = wb.getSheetAt(0);
            int endRow = sheet.getLastRowNum();

            for (int i = firstRecordNumber; i <= endRow; i++) {

                row = sheet.getRow(i);
                if (row.getFirstCellNum() == -1) {
                    break;
                }
                //   System.out.println(sheet.getSheetName() + " reading " + i);


                for (int j = row.getFirstCellNum(); j < 50; j++) {
                    Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

                    switch (cell.getCellType()) {
                        case STRING:
                            cellData.add(cell.getStringCellValue().toUpperCase().replace("\n", ""));
                            break;
                        case NUMERIC:
                            if (DateUtil.isCellDateFormatted(cell)) {
                                DataFormatter formatter = new DataFormatter();
                                String formattedValue = formatter.formatCellValue(cell);
                                //  System.out.println("Using DataFormatter: " + formattedValue);
                                cellData.add(formattedValue);
                                break;
                            }

                            Double cellValue = cell.getNumericCellValue();
                            Integer cellValueInt = cellValue.intValue();
                            String cellValueText = cellValueInt.toString();
                            cellData.add(cellValueText);
                            break;
                        case BLANK:
                            cellData.add("_");
                            break;
                        default:
                            cellData.add("_");
                    }

                }
                List<String> dataGrip = new ArrayList<String>(cellData);
                sourceMap.put(i + 1, dataGrip);
                cellData.clear();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return sourceMap;
    }

    @Override
    public Path writeToFileSystem(String directory, MultipartFile file, String fileName){

        StringBuilder fileNames = new StringBuilder();

        Path fileNameAndPath = Paths.get(String.valueOf(directory),fileName+".xlsx");

        fileNames.append(file.getOriginalFilename()).append(" ");
        try {
            Files.write(fileNameAndPath, file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileNameAndPath;
    }

    @Override
    public boolean writeToExcel(Map<String, Object[]> source, String directoryName, String nameTable) throws IOException {
        //data.put("1", new Object[]{ "ID", "NAME", "LASTNAME" }); for info

        // Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook();

        String pathToFile = getPathBySystem.getPathBySystem(directoryName);

        File fileDirectory = new File(pathToFile);
        if(!fileDirectory.exists()){
            Files.createDirectory(Paths.get(pathToFile));
        }

        // Create a blank sheet
        XSSFSheet sheet = workbook.createSheet(nameTable);


        // Iterate over data and write to sheet
        Set<String> keySet = source.keySet();
        int rownum = 0;
        for (String key : keySet) {
            // this creates a new row in the sheet
            Row row = sheet.createRow(rownum++);
            Object[] objArr = source.get(key);
            int cellnum = 0;
            for (Object obj : objArr) {
                // this line creates a cell in the next column of that row
                Cell cell = row.createCell(cellnum++);
                if (obj instanceof String)
                    cell.setCellValue((String) obj);
                else if (obj instanceof Integer)
                    cell.setCellValue((Integer) obj);
            }
        }
        try {
            // this Writes the workbook to file on disk
            FileOutputStream out = new FileOutputStream(new File(fileDirectory + File.separator + nameTable + ".xlsx"));
            workbook.write(out);
            out.close();
            System.out.println(nameTable + ".xlsx written successfully on disk.");
            return true;
        } catch (Exception e) {
            loggerHandler.loggerHandler("eu.pnlpnr.ems.service.fileManipulation",e.getMessage(),"WARNING","error");
            loggerHandler.loggerHandler("eu.pnlpnr.ems.service.fileManipulation","Writing excel table with name: "+ nameTable + " not finished!","WARNING","error");
            e.printStackTrace();
        }

        return false;


    }

    @Override
      // write doc to output stream
    public void writeXml(Document doc, OutputStream output) throws TransformerException, UnsupportedEncodingException {

        TransformerFactory transformerFactory = TransformerFactory.newInstance();

        // The default add many empty new line, not sure why?
        // https://mkyong.com/java/pretty-print-xml-with-java-dom-and-xslt/
        Transformer transformer = transformerFactory.newTransformer();

        // pretty print
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "no");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(output);

        transformer.transform(source, result);

    }

    @SneakyThrows
    @Override
    public void copyFile(String originalPathSource, String destinationPath) {
        File destinationFilePath = new File(destinationPath);
        if (!destinationFilePath.exists()){
            //noinspection ResultOfMethodCallIgnored
            destinationFilePath.mkdirs();
        }
        Path copied = Paths.get(destinationPath);
        Path originalPath = Paths.get(originalPathSource);
        Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public Map<Integer, List<String>> readPassengersFromXlsx(Path fileNameAndPath, Long flightId, Integer rowsToRead,Integer firstRowNum) {
        Map<Integer, List<String>> passengersPerFlight = new HashMap<>();
        try {
            FileInputStream excelFile = new FileInputStream(new File(String.valueOf(fileNameAndPath)));
            XSSFWorkbook wb = new XSSFWorkbook(excelFile);
            Sheet sheet = wb.getSheetAt(0);
            Integer endRow = sheet.getLastRowNum();

            Row row;
            Integer rowCounter = 0;
            int counter = 0;
            List<String> cellData = new ArrayList<>();

            for (int i = firstRowNum; i <= endRow; i++) {
                counter = 0;
                row = sheet.getRow(i);
                for (int j = row.getFirstCellNum(); j < rowsToRead; j++) {
                    Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
//                    if (counter > row.getLastCellNum()) {
//                        counter = row.getLastCellNum();
//                    }

                    switch (cell.getCellType()) {
                        case STRING:
                            cellData.add(cell.getStringCellValue().toUpperCase());
                            break;
                        case NUMERIC:
                            XSSFCellStyle style = (XSSFCellStyle) cell.getCellStyle();
                            if (DateUtil.isCellDateFormatted(cell)) {
                                Date date = cell.getDateCellValue();
                                cellData.add(date.toString());
                                break;
                            }

                            Double cellValue = cell.getNumericCellValue();
                            Integer cellValueInt = cellValue.intValue();
                            String cellValueText = cellValueInt.toString();
                            cellData.add(cellValueText);
                            break;
                        case BLANK:
                            cellData.add(" ");
                            break;
                        default:
                            cellData.add(" ");

                    }
                    counter++;
                }
                if (!cellData.get(3).equals(" ")) {

                    List<String> rowData = new ArrayList<>(cellData);
                    passengersPerFlight.put(rowCounter, rowData);
                    rowCounter++;
                    cellData.clear();
                }
                cellData.clear();
            }
        } catch (Exception e) {
            System.out.println("Wrong file path.");
        }
        return passengersPerFlight;
    }



}
