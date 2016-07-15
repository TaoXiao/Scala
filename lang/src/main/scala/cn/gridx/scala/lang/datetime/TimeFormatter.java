package cn.gridx.scala.lang.datetime;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by tao on 5/26/16.
 */
public class TimeFormatter {
    public static void main(String[] args) {
        DateTimeZone tz = DateTimeZone.forID("America/Los_Angeles");

        /*
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTime day = format.parseDateTime("2016-05-06 12:34:56");
        System.out.println(day);
        System.out.println(day.getMillis());
        */

        long seconds = 1456876800L;
        DateTime day2 = new DateTime(seconds*1000, DateTimeZone.UTC);
        System.out.println(day2);
        DateTime day3 = new DateTime(convertFromUTC(seconds*1000, tz));
        System.out.println(day3);


        /*
        long ts = 1456876800L;
        DateTime d = convertFromUTC(ts*1000, tz);
        System.out.println(d);

        System.out.println(new DateTime(ts*1000, tz));
        */

        /*
        DateTime t1 = new DateTime();
        DateTime t2 = new DateTime(tz);
        System.out.println(t1);
        System.out.println(t2);

        long millis_1 = t1.getMillis();
        long millis_2 = t2.getMillis();
        System.out.println(millis_1);
        System.out.println(millis_2);

        System.out.println(new DateTime(millis_1));
        System.out.println(new DateTime(millis_2, tz));
        */
    }


    public static DateTime convertFromUTC(long millis, DateTimeZone newtz)  {
        DateTime newT2 = new DateTime(millis, DateTimeZone.UTC);
        try {
            DateTime newTime = new DateTime(newT2.getYear(), newT2.getMonthOfYear(), newT2.getDayOfMonth(),
                    newT2.getHourOfDay(), newT2.getMinuteOfHour(), newT2.getMillisOfSecond(), newtz);
            return newTime;
        } catch (Throwable e){
             System.out.println(e);
        }

        return newT2;
    }

    public static long convertToUTC(long millis) {
        DateTime newT2 = new DateTime(millis, DateTimeZone.UTC);
        return newT2.getMillis();
    }
}
