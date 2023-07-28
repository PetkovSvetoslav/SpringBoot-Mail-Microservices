package mail.microservices.config;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateAndTimeFormat {

    public String returnTimeStamp(){
        DateTimeFormatter dateFormatter  = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        LocalDateTime localTime = LocalDateTime.now();
        return dateFormatter.format(localTime);
    }
    public String returnTimeStampShort(){
        DateTimeFormatter dateFormatter  = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDateTime localTime = LocalDateTime.now();
        return dateFormatter.format(localTime);
    }
    public String convertDateToStringTimeNow(Date date) {
       DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       return dateFormatter.format(date);
    }

    public String returnTimeAndDateNow(){
        DateTimeFormatter dateFormatter  = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localTime = LocalDateTime.now();
        return dateFormatter.format(localTime);

    }

}
