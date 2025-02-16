package kr.gimaek.loader;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class Tools {
    public static String formatDateTime(Date date){
        try{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return simpleDateFormat.format(date);
        } catch (Exception e){
            return "";
        }
    }

    public static String getToday(){
        Date now = new Date();
        return formatDateTime(now);
    }
}
