package mail.microservices.config;


import lombok.SneakyThrows;
import mail.microservices.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerHandler {

    @Autowired
    private EmailService emailService;

    public LoggerHandler() {
    }

    @SneakyThrows
    public void loggerHandler(String packageName, String message, String level, String logFileName) {
        //TODO update method to show class and package

        GetPathBySystem getPathBySystem = new GetPathBySystem();
        Logger logger = Logger.getLogger(packageName);
        String directoryPath = getPathBySystem.getPathBySystem("log");

        // Simple file logging Handler.
        FileHandler fh = new FileHandler(directoryPath + File.separator+ logFileName +".log", true);
        logger.addHandler(fh);
        Runnable runnable = ()->{
            //   sendSimpleEmailMessage(String sender,String subject,String content,String recipient)
            if(level.equals("WARNING")){
                emailService.sendSimpleEmailMessage("support@intellisoft.guru","WARNING EMS SYSTEM","Package name: "+ packageName +"Message: " + message,"support@intellisoft.guru");


            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
        Level l = Level.parse(level);
        logger.log(l ,message);
        fh.close();
    }

    //ChatGPT new version

//    public void loggerHandler(String packageName, String message, String level, String logFileName) throws IOException {
//
//        GetPathBySystem getPathBySystem = new GetPathBySystem();
//        Logger logger = Logger.getLogger(packageName);
//        String directoryPath = getPathBySystem.getPathBySystem("log");
//        File fileLog = new File(directoryPath + File.separator + "log.log");
//        File fileErrorLog = new File(directoryPath + File.separator + "error.log");
//        if (!fileLog.exists()) Files.createFile(Paths.get(fileLog.getAbsolutePath()));
//        if (!fileErrorLog.exists()) Files.createFile(Paths.get(fileErrorLog.getAbsolutePath()));
//
//        FileHandler fh = null;
//        try {
//            // Simple file logging Handler.
//            fh = new FileHandler(directoryPath + File.separator + logFileName + ".log", true);
//            logger.addHandler(fh);
//
//            Runnable runnable = () -> {
//                if (level.equals("WARNING") || level.equals("ERROR")) {
//
//                    TransportEmailWrapper transportEmailWrapper = new TransportEmailWrapper();
//                    // populate transportEmailWrapper with necessary information
//                    try {
//                        emailService.sendSimpleEmailMessage(transportEmailWrapper);
//                    } catch (MessagingException e) {
//                        throw new RuntimeException(e);
//                    }
//
//                }
//            };
//
//            Thread thread = new Thread(runnable);
//            thread.start();
//            SimpleFormatter formatter = new SimpleFormatter();
//            fh.setFormatter(formatter);
//            Level l = Level.parse(level);
//            logger.log(l, message);
//
//        } finally {
//            if (fh != null) {
//                fh.close();
//            }
//        }
//    }



}



