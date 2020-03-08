package cn.jmss2u.seckill.util;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author xgp
 * @version 1.0
 * @date 2020/3/7 15:45
 */
public class TimeUtil {

    /**
     * 获取当前时间为首的五个时段
     * @return
     */
    public static List<Date> getDateMenu(){
        List<Date> dates = new ArrayList<>();
        Date start = startOfDay();
        for (int i = 0; i < 12; i++) {
            dates.add(addDateHour(start, 2*i));
        }
        //当前时间
        Date now = new Date();
        for (Date d : dates){
            if (d.getTime()<=now.getTime()&&now.getTime()<addDateHour(d, 2).getTime()){
                now = d;
                break;
            }
        }
        List<Date> res = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            res.add(addDateHour(now, 2*i));
        }
        return res;
    }

    private static Date startOfDay(){
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DAY_OF_MONTH, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }


    public static Date addDateHour(Date date, int hour){
        Date res = new Date();
        res.setTime(date.getTime()+hour * 60 *60 *1000);
        return res;
    }


    public static String date2Str(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHH");
        return simpleDateFormat.format(date);
    }
}
