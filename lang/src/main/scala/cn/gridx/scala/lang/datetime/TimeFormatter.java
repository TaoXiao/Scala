package cn.gridx.scala.lang.datetime;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

/**
 * Created by tao on 5/26/16.
 */
public class TimeFormatter {
    public static void main(String[] args) {
        DateTimeZone.setDefault(DateTimeZone.forID("America/Los_Angeles"));
        DateTime day = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseDateTime("2016-05-06 12:34:56");
        System.out.println(day);
    }
}
