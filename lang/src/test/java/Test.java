import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Created by tao on 5/24/16.
 */
public class Test {
    public static void main(String[] args) {
        System.out.println("ok");
        DateTimeZone tz = DateTimeZone.forID("America/Los_Angeles");
        DateTime start = new DateTime(1467702000000L, tz);
        DateTime end = new DateTime(1467701999000L, tz);
        System.out.println(start);
        System.out.println(end);
    }
}
